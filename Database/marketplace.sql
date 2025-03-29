--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4
-- Dumped by pg_dump version 17.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: CategoryEntity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."CategoryEntity" (
    id bigint NOT NULL,
    name text
);


ALTER TABLE public."CategoryEntity" OWNER TO postgres;

--
-- Name: CategoryEntity_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."CategoryEntity_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public."CategoryEntity_id_seq" OWNER TO postgres;

--
-- Name: CategoryEntity_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."CategoryEntity_id_seq" OWNED BY public."CategoryEntity".id;


--
-- Name: ImageEntity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."ImageEntity" (
    id bigint NOT NULL,
    "productId" bigint,
    bytes bytea,
    extension text
);


ALTER TABLE public."ImageEntity" OWNER TO postgres;

--
-- Name: ImageEntity_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."ImageEntity_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public."ImageEntity_id_seq" OWNER TO postgres;

--
-- Name: ImageEntity_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."ImageEntity_id_seq" OWNED BY public."ImageEntity".id;


--
-- Name: ProductEntity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."ProductEntity" (
    id bigint NOT NULL,
    "sellerId" bigint NOT NULL,
    "categoryId" bigint NOT NULL,
    title text,
    description text,
    price double precision
);


ALTER TABLE public."ProductEntity" OWNER TO postgres;

--
-- Name: ProductEntity_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."ProductEntity_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public."ProductEntity_id_seq" OWNER TO postgres;

--
-- Name: ProductEntity_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."ProductEntity_id_seq" OWNED BY public."ProductEntity".id;


--
-- Name: RoleEntity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."RoleEntity" (
    id bigint NOT NULL,
    name text
);


ALTER TABLE public."RoleEntity" OWNER TO postgres;

--
-- Name: RoleEntity_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."RoleEntity_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public."RoleEntity_id_seq" OWNER TO postgres;

--
-- Name: RoleEntity_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."RoleEntity_id_seq" OWNED BY public."RoleEntity".id;


--
-- Name: UserEntity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."UserEntity" (
    id bigint NOT NULL,
    username text,
    "passHash" text,
    phone text
);


ALTER TABLE public."UserEntity" OWNER TO postgres;

--
-- Name: UserEntity_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."UserEntity_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public."UserEntity_id_seq" OWNER TO postgres;

--
-- Name: UserEntity_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."UserEntity_id_seq" OWNED BY public."UserEntity".id;


--
-- Name: UserRole; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."UserRole" (
    "userId" bigint NOT NULL,
    "roleId" bigint NOT NULL
);


ALTER TABLE public."UserRole" OWNER TO postgres;

--
-- Name: CategoryEntity id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."CategoryEntity" ALTER COLUMN id SET DEFAULT nextval('public."CategoryEntity_id_seq"'::regclass);


--
-- Name: ImageEntity id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."ImageEntity" ALTER COLUMN id SET DEFAULT nextval('public."ImageEntity_id_seq"'::regclass);


--
-- Name: ProductEntity id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."ProductEntity" ALTER COLUMN id SET DEFAULT nextval('public."ProductEntity_id_seq"'::regclass);


--
-- Name: RoleEntity id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."RoleEntity" ALTER COLUMN id SET DEFAULT nextval('public."RoleEntity_id_seq"'::regclass);


--
-- Name: UserEntity id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."UserEntity" ALTER COLUMN id SET DEFAULT nextval('public."UserEntity_id_seq"'::regclass);


--
-- Name: CategoryEntity CategoryEntity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."CategoryEntity"
    ADD CONSTRAINT "CategoryEntity_pkey" PRIMARY KEY (id);


--
-- Name: ImageEntity ImageEntity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."ImageEntity"
    ADD CONSTRAINT "ImageEntity_pkey" PRIMARY KEY (id);


--
-- Name: ProductEntity ProductEntity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."ProductEntity"
    ADD CONSTRAINT "ProductEntity_pkey" PRIMARY KEY (id);


--
-- Name: RoleEntity RoleEntity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."RoleEntity"
    ADD CONSTRAINT "RoleEntity_pkey" PRIMARY KEY (id);


--
-- Name: UserEntity UserEntity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."UserEntity"
    ADD CONSTRAINT "UserEntity_pkey" PRIMARY KEY (id);


--
-- Name: UserRole UserRole_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."UserRole"
    ADD CONSTRAINT "UserRole_pkey" PRIMARY KEY ("userId", "roleId");


--
-- Name: UserEntity unique_username; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."UserEntity"
    ADD CONSTRAINT unique_username UNIQUE (username);


--
-- Name: ImageEntity ImageEntity_productId_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."ImageEntity"
    ADD CONSTRAINT "ImageEntity_productId_fkey" FOREIGN KEY ("productId") REFERENCES public."ProductEntity"(id) ON UPDATE CASCADE ON DELETE CASCADE NOT VALID;


--
-- Name: ProductEntity ProductEntity_categoryId_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."ProductEntity"
    ADD CONSTRAINT "ProductEntity_categoryId_fkey" FOREIGN KEY ("categoryId") REFERENCES public."CategoryEntity"(id) ON UPDATE CASCADE ON DELETE CASCADE NOT VALID;


--
-- Name: ProductEntity ProductEntity_sellerId_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."ProductEntity"
    ADD CONSTRAINT "ProductEntity_sellerId_fkey" FOREIGN KEY ("sellerId") REFERENCES public."UserEntity"(id) ON UPDATE CASCADE ON DELETE CASCADE NOT VALID;


--
-- Name: UserRole UserRole_roleId_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."UserRole"
    ADD CONSTRAINT "UserRole_roleId_fkey" FOREIGN KEY ("roleId") REFERENCES public."RoleEntity"(id) ON UPDATE CASCADE ON DELETE CASCADE NOT VALID;


--
-- Name: UserRole UserRole_userId_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."UserRole"
    ADD CONSTRAINT "UserRole_userId_fkey" FOREIGN KEY ("userId") REFERENCES public."UserEntity"(id) ON UPDATE CASCADE ON DELETE CASCADE NOT VALID;


--
-- PostgreSQL database dump complete
--

