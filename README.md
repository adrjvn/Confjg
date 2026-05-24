# Confjg

Confjg to lekka i elastyczna biblioteka Java zaprojektowana w celu uproszczenia zarządzania konfiguracją dla Twoich aplikacji. Zapewnia łatwy sposób definiowania, ładowania i zapisywania konfiguracji z różnych źródeł, w tym plików JSON i MongoDB.

## Funkcje

*   **Konfiguracja sterowana adnotacjami**: Definiuj metadane konfiguracji za pomocą adnotacji `@ConfjgInfo`.
*   **Obsługa plików JSON**: Bezproblemowo ładuj i zapisuj konfiguracje do plików JSON za pomocą Gson.
*   **Obsługa MongoDB**: Integruj się z MongoDB w celu trwałego przechowywania konfiguracji (wymaga implementacji `MongoConfjg`).
*   **Operacje asynchroniczne**: Zapisuj konfiguracje asynchronicznie, aby uniknąć blokowania głównego wątku.
*   **Elastyczne API**: `ConfjgManager` z wzorcem budowniczego dla łatwej konfiguracji i rejestracji klas konfiguracyjnych.
*   **Konfigurowalny Gson**: Konfiguruj instancje Gson z niestandardowymi adapterami typów i zasadami nazewnictwa pól.

## Użycie

### 1. Zdefiniuj swoją klasę konfiguracji

Utwórz klasę, która rozszerza `Confjg` i opatrz ją adnotacją `@ConfjgInfo`. Ta klasa będzie przechowywać Twoje właściwości konfiguracyjne.

```java
package com.example.config;

import me.adrjan.confjg.Confjg;
import me.adrjan.confjg.annotation.ConfjgInfo;

@ConfjgInfo(name = "myconfig", path = "config") // Tworzy 'config/myconfig.json'
public class MyConfig extends Confjg {

    public String appName = "Moja Wspaniała Aplikacja";
    public int maxUsers = 100;
    public DatabaseSettings database = new DatabaseSettings();

    public static class DatabaseSettings {
        public String host = "localhost";
        public int port = 27017;
    }
}
```

### 2. Zainicjuj i zarejestruj za pomocą ConfjgManager

Użyj `ConfjgManager.Builder`, aby utworzyć instancję `ConfjgManager` i zarejestrować swoją klasę konfiguracji.

```java
package com.example;

import com.example.config.MyConfig;
import me.adrjan.confjg.ConfjgManager;

public class Main {

    public static void main(String[] args) throws Exception {
        ConfjgManager confjgManager = new ConfjgManager.Builder()
                // Tutaj możesz dostosować Gson, np. dodać adaptery typów
                // .addGsonAdapter(MyCustomType.class, new MyCustomTypeAdapter())
                .build();

        // Zarejestruj swoją klasę konfiguracji
        MyConfig config = confjgManager.registerConfjg(MyConfig.class);

        // Dostęp do właściwości konfiguracji
        System.out.println("Nazwa aplikacji: " + config.appName);
        System.out.println("Maksymalna liczba użytkowników: " + config.maxUsers);
        System.out.println("Host bazy danych: " + config.database.host);

        // Zmodyfikuj właściwość
        config.maxUsers = 150;

        // Zapisz zmiany (spowoduje to aktualizację 'config/myconfig.json')
        config.save();

        // Lub zapisz asynchronicznie
        config.saveAsync().thenRun(() -> System.out.println("Konfiguracja zapisana asynchronicznie!"));

        // Przeładuj konfigurację z pliku
        config.reload();
        System.out.println("Przeładowana maksymalna liczba użytkowników: " + config.maxUsers);
    }
}
```

Po pierwszym uruchomieniu tego kodu, w katalogu głównym Twojej aplikacji zostanie utworzony katalog `config`, a w nim `myconfig.json` zostanie wygenerowany z domyślnymi wartościami z `MyConfig`. Kolejne uruchomienia będą ładować dane z tego pliku.

### Konfiguracja MongoDB (zaawansowane)

W przypadku konfiguracji opartej na MongoDB, rozszerzyłbyś `MongoConfjg` (nie pokazano tutaj, ale zasada jest podobna) i zarejestrował `MongoClient` za pomocą `ConfjgManager.Builder`.

```java
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

// ... wewnątrz metody main lub konfiguracji
MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");

ConfjgManager mongoConfjgManager = new ConfjgManager.Builder()
        .registerMongoClient(mongoClient)
        .build();

// Zakładając, że MyMongoConfig rozszerza MongoConfjg i ma @ConfjgInfo z bazą danych/kolekcją
MyMongoConfig mongoConfig = mongoConfjgManager.registerConfjg(MyMongoConfig.class);

// ... użyj mongoConfig
mongoClient.close();
```