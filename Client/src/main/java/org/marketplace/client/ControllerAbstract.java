package org.marketplace.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class ControllerAbstract {
    protected ObjectOutputStream out;
    protected ObjectInputStream in;

    public void setConnection(ObjectInputStream in, ObjectOutputStream out) {
        this.in = in;
        this.out = out;
    }
}
