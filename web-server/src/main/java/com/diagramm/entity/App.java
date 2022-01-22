package com.diagramm.entity;

import lombok.Data;

import java.util.List;

@Data
public class App {

    String name;
    String type;
    List<String> refs;

}
