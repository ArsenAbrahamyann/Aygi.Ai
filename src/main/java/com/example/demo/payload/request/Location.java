package com.example.demo.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Location {

    private double latitude;
    private double longitude;

}
