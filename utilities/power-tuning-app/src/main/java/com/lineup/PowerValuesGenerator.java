package com.lineup;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PowerValuesGenerator {

  public static void main(String[] args) {

    String values = Stream
        .concat(
            Stream.iterate(256,  i -> i <  1024, i -> i + 256 ),
            Stream.iterate(1024, i -> i <= 4096, i -> i + 1024)
        )
        .sorted()
        .map(Object::toString)
        .collect(Collectors.joining(",", "[", "]"));

    System.out.println(values);
  }

}
