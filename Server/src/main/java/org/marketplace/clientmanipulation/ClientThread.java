package org.marketplace.clientmanipulation;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.marketplace.clientmanipulation.objects.ProductPreview;
import org.marketplace.clientmanipulation.objects.ProductView;
import org.marketplace.clientmanipulation.request.*;
import org.marketplace.clientmanipulation.response.CategoryResponse;
import org.marketplace.clientmanipulation.response.ProductPreviewsResponse;
import org.marketplace.clientmanipulation.response.ProductResponse;
import org.marketplace.clientmanipulation.response.Response;
import org.marketplace.persistence.connection.DatabaseConnection;
import org.marketplace.persistence.connection.ParameterPair;
import org.marketplace.persistence.dao.EntityDao;
import org.marketplace.persistence.model.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ClientThread extends Thread {

    Boolean running;

    private Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private DatabaseConnection databaseConnection;
    private UserEntity user;
    private Collection<RoleEntity> roles;

    private EntityDao<UserEntity> userDao;
    private EntityDao<ProductEntity> productDao;
    private EntityDao<CategoryEntity> categoryDao;
    private EntityDao<ImageEntity> imageDao;
    private EntityDao<RoleEntity> roleDao;
    private EntityDao<UserRole> userRoleDao;

    private final int queryLimit = 10;

    private Argon2 argon2;

    public ClientThread(Socket client, DatabaseConnection databaseConnection) {
        this.client = client;

        try {
            this.in = new ObjectInputStream(client.getInputStream());
            this.out = new ObjectOutputStream(client.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.databaseConnection = databaseConnection;
        this.user = null;
        this.roles = null;

        userDao = new EntityDao<UserEntity>(databaseConnection);
        productDao = new EntityDao<ProductEntity>(databaseConnection);
        categoryDao = new EntityDao<CategoryEntity>(databaseConnection);
        imageDao = new EntityDao<ImageEntity>(databaseConnection);
        roleDao = new EntityDao<RoleEntity>(databaseConnection);
        userRoleDao = new EntityDao<UserRole>(databaseConnection);
        argon2 = Argon2Factory.create();
    }

    @Override
    public void run() {
        running = true;
        try {
            while (running) {

                Request request = (Request) in.readObject();

                Response response = processRequest(request);

                if(!running) {
                    System.out.println("Closing " + client.getInetAddress().getHostAddress());
                    client.close();
                    return;
                }
                out.writeObject(response);

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Response processRequest(Request request) {
        if(request == null) {
            return new Response(0,"Invalid request");
        }
        if(request instanceof CloseRequest) {
            running = false;
            return new Response(0,"Client closed");
        }
        if(request instanceof LoginRequest) {
            return processLoginRequest((LoginRequest) request);
        }
        if(request instanceof RegisterRequest) {
            return processRegisterRequest((RegisterRequest) request);
        }
        if(request instanceof CategoryRequest) {
            return processCategoryRequest((CategoryRequest) request);
        }
        if(request instanceof AddProductRequest) {
            return processAddProductRequest((AddProductRequest) request);
        }
        if(request instanceof MyProductsRequest) {
            return processMyProductsRequest((MyProductsRequest) request);
        }
        if(request instanceof ProductRequest) {
            return processProductRequest((ProductRequest) request);
        }
        if(request instanceof RemoveProductRequest) {
            return processRemoveProductRequest((RemoveProductRequest) request);
        }
        if(request instanceof CategoryProductsRequest) {
            return processCategoryProductsRequest((CategoryProductsRequest) request);
        }

        return new Response(0,"Invalid request");
    }

    private Response processCategoryProductsRequest(CategoryProductsRequest request) {
        boolean isNormalUser = false;
        if(roles!=null) {
            for (RoleEntity role : roles) {
                if (role.getName().equals("USER")) {
                    isNormalUser = true;
                    break;
                }
            }
        }
        if(!isNormalUser) {
            return new ProductPreviewsResponse(0,"Invalid access", new ArrayList<>());
        }

        try {
            List<ProductEntity> products = productDao.findAllByParamsLimited(ProductEntity.class, request.getIndex(), queryLimit, new ParameterPair("category.name", request.getCategory()));

            List<ProductPreview> previews = new ArrayList<>();
            for (ProductEntity product : products) {
                ProductPreview preview = new ProductPreview();
                preview.setId(product.getId());
                preview.setTitle(product.getTitle());
                preview.setPrice(product.getPrice());
                Optional<ImageEntity> previewImage = imageDao.findFirstByParams(ImageEntity.class, new ParameterPair("product", product));
                previewImage.ifPresentOrElse(imageEntity -> preview.setImageBytes(imageEntity.getBytes()), () -> preview.setImageBytes(null));
                previews.add(preview);
            }
            return new ProductPreviewsResponse(1, "", previews);
        }
        catch(Exception e) {
            return new ProductPreviewsResponse(0, "Error finding products", new ArrayList<>());
        }
    }

    private Response processRemoveProductRequest(RemoveProductRequest removeProductRequest) {
        boolean isNormalUser = false;
        if(roles!=null) {
            for (RoleEntity role : roles) {
                if (role.getName().equals("USER")) {
                    isNormalUser = true;
                    break;
                }
            }
        }
        if(!isNormalUser) {
            return new ProductResponse(0,"Invalid access", null);
        }
        Optional<ProductEntity> product;
        try {
            product = productDao.findById(ProductEntity.class, removeProductRequest.getId());
        }
        catch(Exception e) {
            return new Response(0,"Could not find product");
        }
        if(product.isPresent()) {
            return new Response(0,"Could not find product");
        }

        if(!product.get().getSeller().equals(user)) {
            return new Response(0,"You don't own this product");
        }


        List<ImageEntity> images;
        try {
            images = imageDao.findAllByParams(ImageEntity.class, new ParameterPair("product", product.get()));
        }
        catch(Exception e) {
            return new Response(0,"Could not find images");
        }

        for(ImageEntity image : images) {
            try {
                imageDao.delete(image);
            }
            catch(Exception e) {
                return new Response(0,"Could not delete an image");
            }
        }

        try {
            productDao.delete(product.get());
        }
        catch(Exception e) {
            return new Response(0,"Could not delete product");
        }

        return new Response(1,"Product deletion successful");
    }

    private Response processProductRequest(ProductRequest productRequest) {
        boolean isNormalUser = false;
        if(roles!=null) {
            for (RoleEntity role : roles) {
                if (role.getName().equals("USER")) {
                    isNormalUser = true;
                    break;
                }
            }
        }
        if(!isNormalUser) {
            return new ProductResponse(0,"Invalid access", null);
        }
        try {
            Optional<ProductEntity> productEntity = productDao.findById(ProductEntity.class, productRequest.getId());
            if(productEntity.isEmpty()) {
                return new ProductResponse(0,"Product not found", null);
            }
            ProductView productView = new ProductView();
            productView.setId(productEntity.get().getId());
            productView.setCategory(productEntity.get().getCategory().getName());
            productView.setDescription(productEntity.get().getDescription());
            productView.setPrice(productEntity.get().getPrice());
            productView.setTitle(productEntity.get().getTitle());
            productView.setSeller(productEntity.get().getSeller().getUsername());
            productView.setPhone(productEntity.get().getSeller().getPhone());

            List<ImageEntity> images = imageDao.findAllByParams(ImageEntity.class, new ParameterPair("product", productEntity.get()));
            List<byte[]> imageBytes = images.stream().map(ImageEntity::getBytes).collect(Collectors.toList());
            productView.setImageBytes(imageBytes);
            if(this.user.equals(productEntity.get().getSeller())) {
                return new ProductResponse(1,"", productView);
            }
            return new ProductResponse(0,"", productView);
        }
        catch(Exception e) {
            return new ProductResponse(0,"Error fetching product", null);
        }
    }

    private Response processMyProductsRequest(MyProductsRequest request) {
        boolean isNormalUser = false;
        if(roles!=null) {
            for (RoleEntity role : roles) {
                if (role.getName().equals("USER")) {
                    isNormalUser = true;
                    break;
                }
            }
        }
        if(!isNormalUser) {
            return new ProductPreviewsResponse(0,"Invalid access", new ArrayList<>());
        }

        try {
            List<ProductEntity> products = productDao.findAllByParamsLimited(ProductEntity.class, request.getIndex(), queryLimit, new ParameterPair("seller", this.user));

            List<ProductPreview> previews = new ArrayList<>();
            for (ProductEntity product : products) {
                ProductPreview preview = new ProductPreview();
                preview.setId(product.getId());
                preview.setTitle(product.getTitle());
                preview.setPrice(product.getPrice());
                Optional<ImageEntity> previewImage = imageDao.findFirstByParams(ImageEntity.class, new ParameterPair("product", product));
                previewImage.ifPresentOrElse(imageEntity -> preview.setImageBytes(imageEntity.getBytes()), () -> preview.setImageBytes(null));
                previews.add(preview);
            }
            return new ProductPreviewsResponse(1, "", previews);
        }
        catch(Exception e) {
            return new ProductPreviewsResponse(0, "Error finding products", new ArrayList<>());
        }

    }

    private Response processAddProductRequest(AddProductRequest addProductRequest) {
        boolean isNormalUser = false;
        if(roles!=null) {
            for (RoleEntity role : roles) {
                if (role.getName().equals("USER")) {
                    isNormalUser = true;
                    break;
                }
            }
        }
        if(!isNormalUser) {
            return new Response(0,"Invalid access");
        }


        if(addProductRequest.getCategory() == null || addProductRequest.getCategory().isEmpty()) {
            return new Response(0,"Invalid category");
        }
        Optional<CategoryEntity> category;
        try {
            category = categoryDao.findFirstByParams(CategoryEntity.class, new ParameterPair("name", addProductRequest.getCategory()));
            if(category.isEmpty())
                return new Response(0,"Invalid category");
        } catch (Exception e) {
            return new Response(0,"Error setting category");
        }

        if(addProductRequest.getTitle() == null || addProductRequest.getTitle().isEmpty()) {
            return new Response(0,"Invalid title");
        }

        if(addProductRequest.getDescription() == null || addProductRequest.getDescription().isEmpty()) {
            return new Response(0,"Invalid description");
        }

        if(addProductRequest.getPrice()==null || addProductRequest.getPrice().isEmpty()) {
            return new Response(0,"Invalid price");
        }
        double price = -1.0d;
        try {
            price = Double.parseDouble(addProductRequest.getPrice());
            if(price < 0)
                return new Response(0,"Negative price");
        } catch (NumberFormatException e) {
            return new Response(0, "Invalid price. Try something like \"150\" or \"150.25\"");
        }

        try {
            Optional<ProductEntity> alreadyExistingProduct = productDao.findFirstByParams(ProductEntity.class,
                    new ParameterPair("title", addProductRequest.getTitle()),
                    new ParameterPair("description", addProductRequest.getDescription()),
                    new ParameterPair("price", price)
                    );
            if(alreadyExistingProduct.isPresent()) {
                return new Response(0,"Product already exists");
            }
        } catch (Exception e) {
            return new Response(0,"Error while looking for product duplicate");
        }

        ProductEntity product = new ProductEntity();

        product.setTitle(addProductRequest.getTitle());
        product.setDescription(addProductRequest.getDescription());
        product.setPrice(price);
        product.setCategory(category.get());
        product.setSeller(user);

        try {
            productDao.save(product);
        } catch (Exception e) {
            return new Response(0,"Error while saving product");
        }
        ProductEntity savedProduct;
        try {
            Optional<ProductEntity> databaseProduct = productDao.findFirstByParams(ProductEntity.class,
                    new ParameterPair("title", product.getTitle()),
                    new ParameterPair("description", product.getDescription()),
                    new ParameterPair("price", product.getPrice())
            );

            if(databaseProduct.isEmpty())
                return new Response(0,"Saving error: Product could not be saved");

            savedProduct=databaseProduct.get();
        } catch (Exception e) {
            return new Response(0,"Error while saving product");
        }


        if(addProductRequest.getImages() != null && !addProductRequest.getImages().isEmpty()) {
            for(byte[] imageBytes : addProductRequest.getImages()) {

                ImageEntity image = new ImageEntity();
                image.setBytes(imageBytes);
                image.setProduct(savedProduct);

                try {
                    imageDao.save(image);
                } catch (Exception e) {
                    return new Response(0,"Error while saving images");
                }

            }
        }


        return new Response(1,"Added product successfully");
    }




    private Response processCategoryRequest(CategoryRequest request) {

        boolean isNormalUser = false;
        for(RoleEntity role : roles) {
            if(role.getName().equals("USER"))
                isNormalUser = true;
        }
        if(!isNormalUser) {
            return new CategoryResponse(0,"Invalid access", new ArrayList<>());
        }

        try {
            List<CategoryEntity> categories = categoryDao.findAll(CategoryEntity.class);
            List<String> categoriesNames = categories.stream().map(CategoryEntity::getName).sorted().collect(Collectors.toList());
            return new CategoryResponse(1, "", categoriesNames);
        } catch (Exception e) {
            return new CategoryResponse(0,"Could not provide categories. Please try again later.", new ArrayList<>());
        }
    }



    private Response processLoginRequest(LoginRequest request) {
        try {
            Optional<UserEntity> foundUser = userDao.findFirstByParams(UserEntity.class, new ParameterPair("username", request.getUsername()));
            if(foundUser.isEmpty())
                return new Response(0,"User not found.");

            if(argon2.verify(foundUser.get().getPassHash(),request.getPassword())) {

                this.user = foundUser.get();

                if(this.user.getRoles()==null) {
                    Collection<UserRole> userRoles = userRoleDao.findAllByParams(UserRole.class, new ParameterPair("id.userId", this.user.getId()));
                    this.roles = new ArrayList<>();
                    for(UserRole userRole : userRoles) {
                        this.roles.add(userRole.getRole());
                    }
                }else this.roles = user.getRoles();

                return new Response(1,"Successfully logged in.");
            } else {
                return new Response(0,"Wrong password.");
            }
        }
        catch (Exception e) {
            return new Response(0,"An error occurred while logging in :( Please try again later.");
        }
    }



    private Response processRegisterRequest(RegisterRequest request) {
        try {
            Optional<UserEntity> foundUser = userDao.findFirstByParams(UserEntity.class, new ParameterPair("username", request.getUsername()));
            if(foundUser.isPresent())
                return new Response(0,"User already exists.");


            if(!Pattern.compile("^[0-9a-z]{4,20}$").matcher(request.getUsername()).matches())
                return new Response(0,"Invalid username");
            if(!Pattern.compile("^\\S{4,20}$").matcher(request.getPassword()).matches())
                return new Response(0,"Invalid password");
            if(!Pattern.compile("^\\+?[0-9]{8,15}$").matcher(request.getPhoneNumber()).matches())
                return new Response(0,"Invalid phone number");


            UserEntity newUser = new UserEntity();
            newUser.setUsername(request.getUsername());
            newUser.setPassHash(argon2.hash(3,1024,1,request.getPassword()));
            newUser.setPhone(request.getPhoneNumber());

            userDao.save(newUser);
            UserEntity createdUser = userDao.findFirstByParams(UserEntity.class, new ParameterPair("username", request.getUsername())).orElse(null);

            RoleEntity role = roleDao.findFirstByParams(RoleEntity.class, new ParameterPair("name", "USER")).orElse(null);
            if(role == null) {
                return new Response(0,"An error occurred while creating your account :( Please try again later.");
            }

            UserRole userRole = new UserRole();
            userRole.setUser(createdUser);
            userRole.setRole(role);

            userRoleDao.save(userRole);

            return new Response(1,"Successfully created your account.");
        }
        catch (Exception e) {
            return new Response(0,"An error occurred while creating your account :( Please try again later.");
        }
    }




}
