package com.demo.kafka.calculatrice;

public record EventMessage(String operator, Integer left, Integer right) {

}
