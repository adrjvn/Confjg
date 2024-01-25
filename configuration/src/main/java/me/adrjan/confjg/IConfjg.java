package me.adrjan.confjg;

import java.util.concurrent.CompletableFuture;

public interface IConfjg {

    void save();

    CompletableFuture<Void> saveAsync();

    void reload();
}