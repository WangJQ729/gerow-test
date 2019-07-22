package com.jq.test.controller;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PostBody {
    @NotNull(message = "参数1不应该为空")
    private Double num1;
    @NotNull(message = "参数2不应该为空")
    private Double num2;
}
