package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
   private String message;
   private Map<String, String> errors;

   public MessageResponse(String message) {
      this.message = message;
   }
}
