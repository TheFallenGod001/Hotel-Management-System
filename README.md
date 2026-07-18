# Hotel Management System

A JavaFX desktop application for managing a hotel; handling rooms, customers, stays, billing, and invoicing, backed by a custom, CSV based persistence layer and organized into proper model, DAO, service, and UI layers.

## Features

* Room management, including room type (Regular, Deluxe, Suite), capacity, floor, occupancy, and maintenance status
* A composable amenities system; rooms can carry any combination of Sea View, Attached Bath, AC, and WIFI, each with its own cost contribution
* Customer records and stay tracking, including check in and check out times
* Billing and invoicing, with automatic tax calculation, per amenity cost breakdown, and support for both individual and group bookings
* Bill querying via predicate based filters (for example, fetching all group invoices)
* A JavaFX GUI with a custom, hand built occupancy gauge widget and animated navigation
* Data stored as plain CSV files and per invoice text files, requiring no external database

## Architecture

The project is organized into four layers, each with a clear responsibility:

| Layer | Responsibility |
|---|---|
| `model` | Plain data classes: `Room`, `Customer`, `Payment`, `StayRecord`, `TimeInfo`, and the `Amenities` hierarchy |
| `dao` | File I/O only; reading, writing, and querying CSV records and invoice text files |
| `service` | Business logic; billing calculations, booking rules, customer handling, orchestrated via `HotelService` |
| `UI` | The JavaFX application and its custom components |

Each layer only talks to the one below it; the UI calls into `HotelService`, which calls into the service classes, which call into the DAOs. Nothing in the service or UI layers touches a file directly.

## The Amenities System

Rooms hold a `Set<Amenities>`, where `SeaView`, `AttachedBath`, `AC`, and `WIFI` each extend an abstract `Amenities` base class. Every amenity contributes its own additional cost, and `Room.calculateCost()` sums the base rate for the room type and capacity with whatever amenities are attached. Amenities are serialized into and parsed back out of a compact encoding (for example, `WIFI:I_5G|AC:1.2`) as part of each room's CSV line.

## Persistence

There is no external database; each DAO (`RoomDAO`, `CustomerDAO`, `RecordsDAO`, `BillsDAO`) reads and writes its own CSV file under `data/`, with invoices additionally written out as individual text files under `data/Bills/`. Rooms and customers are serialized through hand written `toString()`/`parseString()` methods rather than a generic serialization library, with malformed or corrupted lines skipped and logged during a read rather than aborting the whole load.

## Billing and Invoicing

`BillingService` computes the total cost of a stay from the room's base rate, its nights, and its amenities, applies tax, and generates a formatted, human readable invoice as plain text. Group bookings are supported directly: `processGroupPayment()` bills each customer individually and also produces a single consolidated group invoice summarizing the whole group's charges.

## Known Issues

* `UI/App.java` is close to 1,800 lines in a single class. The `model`, `dao`, and `service` layers are cleanly separated, but the UI layer is not split into individual view or controller classes yet; view construction, navigation, and event handling all live in one file.

## Requirements

* Java 21
* Maven
* JavaFX 21.0.2 (pulled automatically via Maven; see `pom.xml`)

## Running

```bash
mvn clean javafx:run
```

The application expects a `data/` directory next to the working directory for its CSV files and bills; it will be created automatically on first run if missing.

## Roadmap

* [ ] Split `UI/App.java` into separate view and controller classes
* [ ] Replace the hand rolled CSV persistence with a lightweight embedded database, or at least a shared serialization utility instead of per class `toString()`/`parseString()` pairs
* [ ] Add automated tests for the service layer, particularly billing calculations
