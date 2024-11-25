module ubb.scs.map.demosocialnetwork {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.dotenv;
    requires java.sql;

    opens ubb.scs.map.demosocialnetwork.domain;
    exports ubb.scs.map.demosocialnetwork.domain;

    opens ubb.scs.map.demosocialnetwork.repository;
    exports ubb.scs.map.demosocialnetwork.repository;

    opens ubb.scs.map.demosocialnetwork.service;
    exports ubb.scs.map.demosocialnetwork.service;

    opens ubb.scs.map.demosocialnetwork.utils.events;
    opens ubb.scs.map.demosocialnetwork.utils.observer;
    exports ubb.scs.map.demosocialnetwork.utils.events;
    exports ubb.scs.map.demosocialnetwork.utils.observer;

    opens ubb.scs.map.demosocialnetwork to javafx.fxml;
    exports ubb.scs.map.demosocialnetwork;
    exports ubb.scs.map.demosocialnetwork.controller;
    opens ubb.scs.map.demosocialnetwork.controller to javafx.fxml;
}