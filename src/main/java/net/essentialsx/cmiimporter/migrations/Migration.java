package net.essentialsx.cmiimporter.migrations;

public interface Migration {

    void run();

    String getName();

    String getDescription();

    boolean isUserDependent();

}
