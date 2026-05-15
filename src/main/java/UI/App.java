package UI;

import service.HotelService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.stage.Stage; // The top-level JavaFX container
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label; // Example UI control
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.layout.StackPane; // Example layout container
import javafx.scene.layout.TilePane;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.Node;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.util.Duration;
import model.Customer;
import model.Room;
import model.roomUtil.Amenities;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ListCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.DialogPane;



public class App extends Application {
    private StackPane root;
    private StackPane contentLayer;
    private Scene scene;
    private Pane animationLayer;

    HotelService hotelService = new HotelService();

    public void start(Stage primaryStage) throws Exception {
        root = new StackPane(); 
        scene = new Scene(root, 1000, 600);
        contentLayer = new StackPane();
        
        String cssPath = getClass().getResource("/resource/style.css").toExternalForm();
        scene.getStylesheets().add(cssPath);
        this.animationLayer = new Pane();
        this.animationLayer.setMouseTransparent(true);
        root.getChildren().addAll(contentLayer, animationLayer);
        
        setUpHome();


        primaryStage.setTitle("Hotel Manager Application"); 
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args){
        launch(args);
    }

    public void setUpHome() {
        contentLayer.getChildren().clear();

        contentLayer.getStyleClass().removeAll("home-background", "dashboard-background");
        contentLayer.getStyleClass().add("home-background");

        VBox container = new VBox(15);
        container.setAlignment(Pos.TOP_CENTER);
        container.prefWidthProperty().bind(contentLayer.widthProperty());

        Label titleLabel = new Label("Fortune Inn");
        String cssLayout = 
            "-fx-font-family: 'Segoe UI', Helvetica, Arial, sans-serif; " +
            "-fx-font-size: 36px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #dfc8a9; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 5, 0, 0, 2); " +
            "-fx-padding: 20px 0px 20px 0px;";

        titleLabel.setStyle(cssLayout);

        TilePane grid = new TilePane();
        grid.setPadding(new Insets(50));
        grid.setHgap(30); grid.setVgap(30);
        grid.setAlignment(Pos.TOP_LEFT);

        Image iconImage = new Image(getClass().getResourceAsStream("/resource/bad.png"));
        ImageView iconView = new ImageView(iconImage);
        iconView.setFitWidth(50);
        iconView.setPreserveRatio(true);

        VBox dashboardCard = createHomeCard("Dashboard", iconView, () -> {});

        dashboardCard.setOnMouseClicked(event -> {
            this.expandingAnimation(
                dashboardCard, 
                this.animationLayer,
                scene, 
                () -> {this.setUpDashboard();}, 
                "-fx-other-background-color",
                "#210804",
                "#090201"
            );
        });

        Image writingIcon = new Image(getClass().getResourceAsStream("/resource/quill-pen.png"));
        ImageView writingIconView1 = new ImageView(writingIcon);
        writingIconView1.setFitWidth(50);
        writingIconView1.setPreserveRatio(true);
        ImageView writingIconView2 = new ImageView(writingIcon);
        writingIconView2.setFitWidth(50);
        writingIconView2.setPreserveRatio(true);


        VBox roomCreateCard = createHomeCard("Create Room", writingIconView1, () -> {});
        VBox customerRegisterCard = createHomeCard("Register Customer", writingIconView2, () -> {});

        roomCreateCard.setOnMouseClicked(event -> {
            this.expandingAnimation(
                roomCreateCard, 
                animationLayer, 
                scene, 
                () -> {this.setUpRoomCreation();},
                "-fx-other-background-color" , 
                "#210804",
                "#090201");
        });

        customerRegisterCard.setOnMouseClicked(event -> {
            this.expandingAnimation(
                customerRegisterCard, 
                animationLayer, 
                scene, 
                () -> {this.setUpCustomerRegistration();},
                "-fx-other-background-color" , 
                "#210804",
                "#090201");
        });

        Image enterImage = new Image(getClass().getResourceAsStream("/resource/enter.png"));
        ImageView enterIcon = new ImageView(enterImage);
        enterIcon.setFitWidth(50);
        enterIcon.setPreserveRatio(true);

        VBox checkInCard = createHomeCard("Check In", enterIcon, () -> {});
        checkInCard.setOnMouseClicked(event -> {
            this.expandingAnimation(checkInCard, 
                animationLayer, 
                scene, 
                () -> { this.setUpCheckIn(null, null); }, 
                "-fx-other-background-color" , 
                "#210804",
                "#090201");
        });

        Image checkOutImage = new Image(getClass().getResourceAsStream("/resource/exit.png"));
        ImageView exitIcon = new ImageView(checkOutImage);
        exitIcon.setFitWidth(50);
        exitIcon.setPreserveRatio(true);


        VBox checkOutCard = createHomeCard("Check Out", exitIcon, () -> {});
        checkOutCard.setOnMouseClicked(event -> 
            this.expandingAnimation(checkOutCard, 
                animationLayer, 
                scene, 
                () -> {
                    this.setUpCheckOut(null, null); 
                },
                "-fx-other-background-color" , 
                "#210804",
                "#090201")
        );

        Image paymentImage = new Image(getClass().getResourceAsStream("/resource/money.png"));
        ImageView paymentIcon = new ImageView(paymentImage);
        paymentIcon.setFitWidth(50);
        paymentIcon.setPreserveRatio(true);

        VBox paymentProcessingCard = createHomeCard("Payment Processing", paymentIcon, () -> {});
        paymentProcessingCard.setOnMouseClicked(event -> 
            this.expandingAnimation(paymentProcessingCard, 
                animationLayer, 
                scene, 
                () -> {this.setUpPaymentProcessing();},
                "-fx-other-background-color" , 
                "#210804",
                "#090201")
        );

        Image recordImage = new Image(getClass().getResourceAsStream("/resource/folder.png"));
        ImageView recordIcon = new ImageView(recordImage);
        recordIcon.setFitWidth(50);
        recordIcon.setPreserveRatio(true);

        VBox recordsVewingCard = createHomeCard("Records Viewing", recordIcon, () -> {});
        recordsVewingCard.setOnMouseClicked(event -> 
            this.expandingAnimation(recordsVewingCard, 
                animationLayer, 
                scene, 
                () -> {this.setUpRecordsViewing();},
                "-fx-other-background-color" , 
                "#210804",
                "#090201")
        );


        grid.getChildren().add(dashboardCard);
        grid.getChildren().add(roomCreateCard);
        grid.getChildren().add(customerRegisterCard);
        grid.getChildren().add(checkInCard);
        grid.getChildren().add(checkOutCard);
        grid.getChildren().add(paymentProcessingCard);
        grid.getChildren().add(recordsVewingCard);

        container.getChildren().addAll(titleLabel, grid);

        contentLayer.getChildren().add(container);
    }

    public void setUpDashboard() {
        contentLayer.getChildren().clear();

        contentLayer.getStyleClass().remove("home-background");
        contentLayer.getStyleClass().add("dashboard-background");

        HBox mainDashboardLayout = new HBox(40);
        mainDashboardLayout.setAlignment(Pos.CENTER);
        mainDashboardLayout.prefWidthProperty().bind(contentLayer.widthProperty());
        mainDashboardLayout.prefHeightProperty().bind(contentLayer.heightProperty());

        double radius = 100;
        double strokeWidth = 12.5;
        double containerSize = (radius * 2) + 90; 

        VBox occupancyContainer = new VBox(20);
        occupancyContainer.setAlignment(Pos.CENTER);
        

        occupancyContainer.prefHeightProperty().bind(contentLayer.heightProperty().subtract(40));
        occupancyContainer.setMinWidth(containerSize);
        occupancyContainer.setMaxWidth(containerSize);

   
        UI.helper.OccupancyGauge occupancyGauge = new UI.helper.OccupancyGauge(radius, strokeWidth);
        occupancyGauge.setPercentage(hotelService.getOccupancyRate() * 100);


        Text description = new Text("Percentage of Rooms Occupied");
        description.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        description.setFill(Color.web("#dfc8a9"));
        description.setOpacity(0.8);
        description.setBoundsType(javafx.scene.text.TextBoundsType.VISUAL);

        occupancyContainer.getStyleClass().add("occupancy-container");
        description.getStyleClass().add("gauge-description");

        occupancyContainer.getChildren().addAll(occupancyGauge, description);

        StackPane.setAlignment(occupancyContainer, Pos.CENTER);
        mainDashboardLayout.getChildren().addAll(createCustomerSearcher(), occupancyContainer, createRoomSearcher());

        Image homeIcon = new Image(getClass().getResourceAsStream("/resource/home.png"));
        ImageView homeIconView = new ImageView(homeIcon);
        homeIconView.setFitWidth(30);
        homeIconView.setPreserveRatio(true);

        Button backButton = new Button("Home", homeIconView);
        backButton.getStyleClass().add("back-button");
        
        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(20)); 
        

        backButton.setOnAction(event -> {
            this.closingAnimation(animationLayer, scene, () -> {this.setUpHome();}, "#090201");
        });

        contentLayer.getChildren().addAll(mainDashboardLayout, backButton);
    }

    public void setUpRoomCreation() {
        contentLayer.getChildren().clear();
        contentLayer.getStyleClass().remove("home-background");
        contentLayer.getStyleClass().add("dashboard-background");

        VBox formContainer = new VBox(15);
        formContainer.getStyleClass().add("form-container"); 
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setMaxWidth(400);
        StackPane.setAlignment(formContainer, Pos.CENTER);

        Label title = new Label("Add New Room");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill : #dfc8a9;"); 
        title.getStyleClass().add("label"); 

        TextField roomNoField = new TextField();
        roomNoField.setPromptText("Room Number (e.g., 101)");
        roomNoField.getStyleClass().add("search-input");

        TextField capacityField = new TextField();
        capacityField.setPromptText("Capacity (e.g., 2)");
        capacityField.getStyleClass().add("search-input");

        Label typeLabel = new Label("Room Type:");
        typeLabel.getStyleClass().add("label");

        ToggleGroup typeGroup = new ToggleGroup();
        HBox typeBox = new HBox(15);
        typeBox.setAlignment(Pos.CENTER);
        for (Room.roomType rt : Room.roomType.values()) {
            RadioButton rb = new RadioButton(rt.toString());
            rb.getStyleClass().add("radio-button");
            rb.setToggleGroup(typeGroup);
            rb.setUserData(rt);
            if (rt == Room.roomType.REGULAR) rb.setSelected(true);
            typeBox.getChildren().add(rb);
        }

        Label amenLabel = new Label("Select Amenities:");
        amenLabel.getStyleClass().add("label");

        FlowPane amenityPane = new FlowPane(10, 10);
        amenityPane.setAlignment(Pos.CENTER);
        List<CheckBox> amenityChecks = new ArrayList<>();
        ArrayList<Amenities> amenities = Room.getAvailableAmenities();

        for (Amenities a : amenities) {
            CheckBox cb = new CheckBox(a.returnName());
            cb.getStyleClass().add("check-box");
            cb.setUserData(a);
            amenityChecks.add(cb);
            amenityPane.getChildren().add(cb);
        }

        Label pricePreview = new Label("Estimated Price: ₹0.00");
        pricePreview.getStyleClass().add("label");
        pricePreview.setStyle("-fx-font-size: 13px;");

        Runnable updatePrice = () -> {
            try {
                int cap = capacityField.getText().isEmpty() ? 1 : Integer.parseInt(capacityField.getText());
                Room.roomType selectedType = typeGroup.getSelectedToggle() != null
                        ? (Room.roomType) typeGroup.getSelectedToggle().getUserData()
                        : Room.roomType.REGULAR;

                Set<Amenities> selectedFeatures = new HashSet<>();
                for (CheckBox cb : amenityChecks) {
                    if (cb.isSelected()) selectedFeatures.add((Amenities) cb.getUserData());
                }

                Room preview = new Room(0, selectedType, false, null, cap, false, selectedFeatures);
                pricePreview.setText(String.format("Estimated Price: ₹%.2f / night", preview.getPrice()));
            } catch (NumberFormatException ignored) {
                pricePreview.setText("Estimated Price: —");
            }
        };

        capacityField.textProperty().addListener((o, ov, nv) -> updatePrice.run());
        typeGroup.selectedToggleProperty().addListener((o, ov, nv) -> updatePrice.run());
        amenityChecks.forEach(cb -> cb.selectedProperty().addListener((o, ov, nv) -> updatePrice.run()));

        Label statusLabel = new Label("");

        Button submitBtn = new Button("Register Room");
        submitBtn.getStyleClass().add("action-button");
        submitBtn.setMaxWidth(Double.MAX_VALUE);

        submitBtn.setOnAction(e -> {
            statusLabel.getStyleClass().removeAll("status-success", "status-error");
            statusLabel.setText(""); 
            
            try {
                int roomNo = Integer.parseInt(roomNoField.getText().trim());
                int capacity = Integer.parseInt(capacityField.getText().trim());

                Room.roomType selectedType = typeGroup.getSelectedToggle() != null
                        ? (Room.roomType) typeGroup.getSelectedToggle().getUserData()
                        : Room.roomType.REGULAR;

                Set<Amenities> selected = new HashSet<>();
                for (CheckBox cb : amenityChecks) {
                    if (cb.isSelected()) selected.add((Amenities) cb.getUserData());
                }

                Room newRoom = new Room(roomNo, selectedType, false, null, capacity, false, selected);
                this.hotelService.getRoomService().addRoom(newRoom);


                statusLabel.setText("Room " + roomNo + " created successfully!");
                statusLabel.getStyleClass().add("status-success");


                roomNoField.clear();
                capacityField.clear();
                amenityChecks.forEach(cb -> cb.setSelected(false));
                for (Toggle t : typeGroup.getToggles()) {
                    if (t.getUserData() == Room.roomType.REGULAR) {
                        t.setSelected(true);
                        break;
                    }
                }
                updatePrice.run(); 

            } catch (NumberFormatException ex) {
                statusLabel.setText("Error: Enter valid numbers for Room No. and Capacity.");
                statusLabel.getStyleClass().add("status-error");
            } catch (IllegalArgumentException ex) {
                statusLabel.setText(ex.getMessage());
                statusLabel.getStyleClass().add("status-error");
            }
        });

        formContainer.getChildren().addAll(
            title, roomNoField, capacityField,
            typeLabel, typeBox,
            amenLabel, amenityPane,
            pricePreview, statusLabel, submitBtn
        );

        Image homeIcon = new Image(getClass().getResourceAsStream("/resource/home.png"));
        ImageView homeIconView = new ImageView(homeIcon);
        homeIconView.setFitWidth(30);
        homeIconView.setPreserveRatio(true);

        Button backButton = new Button("Home", homeIconView);
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(event ->
            this.closingAnimation(animationLayer, scene, () -> this.setUpHome(), "#090201")
        );

        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(20));

        contentLayer.getChildren().addAll(backButton, formContainer);
    }

    public void setUpCustomerRegistration() {
        contentLayer.getChildren().clear();
        contentLayer.getStyleClass().remove("home-background");
        contentLayer.getStyleClass().add("dashboard-background");

        VBox formContainer = new VBox(15);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setMaxWidth(400);
        StackPane.setAlignment(formContainer, Pos.CENTER);

        Label title = new Label("Register Customer");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: #dfc8a9; -fx-font-weight: bold;");

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        nameField.getStyleClass().add("search-input");

        TextField contactField = new TextField();
        contactField.setPromptText("Contact Number");
        contactField.getStyleClass().add("search-input");

        TextField dobField = new TextField();
        dobField.setPromptText("Date of Birth (dd/MM/yyyy)  —  optional");
        dobField.getStyleClass().add("search-input");

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 12px;");
        errorLabel.setWrapText(true);

        Label successLabel = new Label("");
        successLabel.setStyle("-fx-text-fill: #90ee90; -fx-font-size: 12px;");

        Button submitBtn = new Button("Register Customer");
        submitBtn.getStyleClass().add("action-button");
        submitBtn.setMaxWidth(Double.MAX_VALUE);

        submitBtn.setOnAction(e -> {
            errorLabel.setText("");
            successLabel.setText("");

            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            String dob = dobField.getText().trim();

            if (name.isEmpty() || contact.isEmpty()) {
                errorLabel.setText("Name and contact are required.");
                return;
            }

            try {
                Customer newCust;
                if (dob.isEmpty()) {
                    newCust = new Customer(name, contact, "01/01/2000", "dd/MM/yyyy");
                    newCust = new Customer(newCust.getCustomerID(), name, contact);
                } else {
                    newCust = new Customer(name, contact, dob, "dd/MM/yyyy");
                }

                this.hotelService.getCustomerService().registerCustomer(newCust);
                successLabel.setText("Registered! ID: " + newCust.getCustomerID());
                nameField.clear();
                contactField.clear();
                dobField.clear();

            } catch (IllegalArgumentException ex) {
                errorLabel.setText(ex.getMessage());
            }
        });

        formContainer.getChildren().addAll(title, nameField, contactField, dobField, errorLabel, successLabel, submitBtn);

        Image homeIcon = new Image(getClass().getResourceAsStream("/resource/home.png"));
        ImageView homeIconView = new ImageView(homeIcon);
        homeIconView.setFitWidth(30);
        homeIconView.setPreserveRatio(true);

        Button backButton = new Button("Home", homeIconView);
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(event ->
            this.closingAnimation(animationLayer, scene, () -> this.setUpHome(), "#090201")
        );

        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(20));

        contentLayer.getChildren().addAll(backButton, formContainer);
    }
    
    public void setUpCheckIn(Customer checkInCustomer, Room checkInRoom) {
        contentLayer.getChildren().clear();
        contentLayer.getStyleClass().remove("home-background");
        contentLayer.getStyleClass().add("dashboard-background");

        VBox formContainer = new VBox(15);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setMaxWidth(450);
        StackPane.setAlignment(formContainer, Pos.CENTER);

        Label title = new Label("Check In");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: #dfc8a9; -fx-font-weight: bold;");

        Label custSectionLabel = new Label("Customer");
        custSectionLabel.setStyle("-fx-text-fill: #dfc8a9; -fx-font-weight: bold; -fx-font-size: 13px;");

        TextField custIDField = new TextField();
        custIDField.setPromptText("Search Name or ID...");
        custIDField.getStyleClass().add("search-input");

        ObservableList<Customer> custItems = FXCollections.observableArrayList();
        custItems.addAll(hotelService.getCustomerService().queryCustomers(c -> !c.hasActiveStay()));
        ListView<Customer> custListView = new ListView<>(custItems);
        custListView.getStyleClass().add("results-area");
        custListView.setPrefHeight(100); 
        
        custListView.setCellFactory(param -> new ListCell<Customer>() {
            protected void updateItem(Customer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item.getCustomerID() + " - " + item.getName());
                    setStyle("-fx-text-fill: #dfc8a9; -fx-background-color: transparent; -fx-padding: 5;");
                }
            }
        });

        Label custInfoLabel = new Label("");
        custInfoLabel.setStyle("-fx-text-fill: #dfc8a9; -fx-font-size: 12px;");

        custListView.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                custIDField.setText(newVal.getCustomerID());
                custInfoLabel.setText(newVal.getName() + "  |  " + newVal.getContact());
            }
        });


        custIDField.textProperty().addListener((obs, old, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                custItems.clear();
            } else {
                ArrayList<Customer> results = hotelService.getCustomerService().queryCustomers(
                    c -> (c.getCustomerID().toLowerCase().contains(newVal.toLowerCase()) && !c.hasActiveStay())|| 
                        (c.getName().toLowerCase().contains(newVal.toLowerCase()) && !c.hasActiveStay())
                );
                custItems.setAll(results);
            }
        });

        Label roomSectionLabel = new Label("Room");
        roomSectionLabel.setStyle("-fx-text-fill: #dfc8a9; -fx-font-weight: bold; -fx-font-size: 13px;");

        TextField roomNoField = new TextField();
        roomNoField.setPromptText("Search available rooms...");
        roomNoField.getStyleClass().add("search-input");

        ObservableList<Room> roomItems = FXCollections.observableArrayList();
        roomItems.addAll(hotelService.getRoomService().getAvailableRooms());
        ListView<Room> roomListView = new ListView<>(roomItems);
        roomListView.getStyleClass().add("results-area");
        roomListView.setPrefHeight(100);

        roomListView.setCellFactory(param -> new ListCell<Room>() {
            protected void updateItem(Room item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText("Room " + item.getRoomNo() + " (" + item.getRoomType() + ")");
                    setStyle("-fx-text-fill: #dfc8a9; -fx-background-color: transparent; -fx-padding: 5;");
                }
            }
        });

        Label roomInfoLabel = new Label("");
        roomInfoLabel.setStyle("-fx-text-fill: #dfc8a9; -fx-font-size: 12px;");

        roomListView.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                roomNoField.setText(String.valueOf(newVal.getRoomNo()));
                roomInfoLabel.setText(newVal.getRoomType() + "  |  ₹" + String.format("%.2f", newVal.getPrice()));
            }
        });

        roomNoField.textProperty().addListener((obs, old, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                roomItems.clear();
            } else {
                ArrayList<Room> available = hotelService.getRoomService().getAvailableRooms();
                available.removeIf(r -> !String.valueOf(r.getRoomNo()).contains(newVal));
                roomItems.setAll(available);
            }
        });

        if (checkInCustomer != null) {
            custIDField.setText(checkInCustomer.getCustomerID());
            custInfoLabel.setText(checkInCustomer.getName() + "  |  " + checkInCustomer.getContact());
        }
        if (checkInRoom != null) {
            roomNoField.setText(String.valueOf(checkInRoom.getRoomNo()));
            roomInfoLabel.setText(checkInRoom.getRoomType() + "  |  ₹" + String.format("%.2f", checkInRoom.getPrice()));
        }

        Label dateSectionLabel = new Label("Check-In Date  —  optional (dd/MM/yyyy)");
        dateSectionLabel.setStyle("-fx-text-fill: #dfc8a9; -fx-font-weight: bold; -fx-font-size: 13px;");
        TextField dateField = new TextField();
        dateField.getStyleClass().add("search-input");

        Label errorLabel = new Label("");
        errorLabel.getStyleClass().add("status-error");
        Label successLabel = new Label("");
        successLabel.getStyleClass().add("status-success");

        Button submitBtn = new Button("Confirm Check In");
        submitBtn.getStyleClass().add("action-button");
        submitBtn.setMaxWidth(Double.MAX_VALUE);

        submitBtn.setOnAction(e -> {
            errorLabel.setText(""); successLabel.setText("");
            try {
                String custID = custIDField.getText().trim();
                int roomNo = Integer.parseInt(roomNoField.getText().trim());
                Room room = this.hotelService.getRoomService().getRoom(roomNo);

                if (!dateField.getText().trim().isEmpty()) {
                    LocalDate date = LocalDate.parse(dateField.getText().trim(), 
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    this.hotelService.getCustomerService().customerCheckIn(custID, room, date);
                } else {
                    this.hotelService.getCustomerService().customerCheckIn(custID, room);
                }

                successLabel.setText("Checked in successfully!");

                custIDField.clear(); custItems.clear(); custInfoLabel.setText("");
                roomNoField.clear(); roomItems.clear(); roomInfoLabel.setText("");
                dateField.clear();
            } catch (Exception ex) {
                errorLabel.setText(ex.getMessage());
            }
        });

        Button proceedToCheckOut = new Button();
        proceedToCheckOut.setText("Proceed to Check Out");
        proceedToCheckOut.getStyleClass().add("action-button");
        proceedToCheckOut.setMaxWidth(Double.MAX_VALUE);

        proceedToCheckOut.setOnAction(e -> {
            this.closingAnimation(animationLayer, scene, () -> {this.setUpCheckOut(checkInCustomer, checkInRoom);}, "#090201");
        });

        formContainer.getChildren().addAll(
            title,
            custSectionLabel, custIDField, custListView, custInfoLabel,
            roomSectionLabel, roomNoField, roomListView, roomInfoLabel,
            dateSectionLabel, dateField,
            errorLabel, successLabel, submitBtn, proceedToCheckOut
        );

        Button backButton = new Button("Home", new ImageView(new Image(getClass().getResourceAsStream("/resource/home.png"))));
        ((ImageView)backButton.getGraphic()).setFitWidth(30);
        ((ImageView)backButton.getGraphic()).setPreserveRatio(true);
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(event -> this.closingAnimation(animationLayer, scene, () -> this.setUpHome(), "#090201"));

        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(20));

        contentLayer.getChildren().addAll(backButton, formContainer);
    }
    
    public void setUpCheckOut(Customer checkOutCustomr, Room checkOutRoom) {
        contentLayer.getChildren().clear();
        contentLayer.getStyleClass().remove("home-background");
        contentLayer.getStyleClass().add("dashboard-background");

        VBox checkOutForm = new VBox(15);
        checkOutForm.setAlignment(Pos.CENTER);
        checkOutForm.setMaxWidth(400);
        StackPane.setAlignment(checkOutForm, Pos.CENTER);

        Label title = new Label("Check Out");
        title.getStyleClass().add("label");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Customer[] selectedCustomer = {null};
        Room[] selectedRoom = {null};
        boolean[] isUpdating = {false};

        Label customerTitle = new Label("Customer");
        customerTitle.getStyleClass().add("label");
        customerTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        TextField customerField = new TextField();
        customerField.setPromptText("Search Name or ID...");
        customerField.getStyleClass().add("search-input");

        ObservableList<Customer> customerItems = FXCollections.observableArrayList();
        customerItems.addAll(hotelService.getCustomerService().getActiveCustomers());
        ListView<Customer> customerView = new ListView<>(customerItems);
        customerView.getStyleClass().add("results-area");
        customerView.setPrefHeight(100);

        Label selectedCustomerLabel = new Label();
        selectedCustomerLabel.getStyleClass().add("label");
        selectedCustomerLabel.setStyle("-fx-font-size: 12px;");

        customerView.setCellFactory(param -> new ListCell<Customer>() {
            protected void updateItem(Customer item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item.getCustomerID() + " - " + item.getName());
                    setStyle("-fx-text-fill: #dfc8a9; -fx-background-color: transparent; -fx-padding: 5;");
                }
            }
        });

        customerField.textProperty().addListener((obs, old, newVal) -> {
            if (isUpdating[0]) return;
            if (newVal == null || newVal.trim().isEmpty()) {
                customerItems.clear();
                selectedCustomer[0] = null;
                selectedCustomerLabel.setText("");
            } else {
                ArrayList<Customer> results = hotelService.getCustomerService().getActiveCustomers();
                results.removeIf(c -> !c.getCustomerID().toLowerCase().contains(newVal.toLowerCase()) && 
                                    !c.getName().toLowerCase().contains(newVal.toLowerCase()));
                customerItems.setAll(results);
            }
        });

        Label roomTitle = new Label("Room");
        roomTitle.getStyleClass().add("label");
        roomTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        TextField roomField = new TextField();
        roomField.setPromptText("Search occupied rooms...");
        roomField.getStyleClass().add("search-input");

        ObservableList<Room> roomItems = FXCollections.observableArrayList();
        roomItems.addAll(hotelService.getRoomService().getOccupiedRooms());
        ListView<Room> roomView = new ListView<>(roomItems);
        roomView.getStyleClass().add("results-area");
        roomView.setPrefHeight(100);

        Label selectedRoomLabel = new Label();
        selectedRoomLabel.getStyleClass().add("label");
        selectedRoomLabel.setStyle("-fx-font-size: 12px;");

        roomView.setCellFactory(param -> new ListCell<Room>() {
            protected void updateItem(Room item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText("Room " + item.getRoomNo() + " (Occupied)");
                    setStyle("-fx-text-fill: #dfc8a9; -fx-background-color: transparent; -fx-padding: 5;");
                }
            }
        });

        roomView.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                isUpdating[0] = true;
                selectedRoom[0] = newVal;
                roomField.setText(String.valueOf(newVal.getRoomNo()));
                selectedRoomLabel.setText("Room " + newVal.getRoomNo() + " | " + newVal.getRoomType());

                
                selectedCustomer[0] = selectedRoom[0].returnCust();
                customerField.setText(selectedCustomer[0].getCustomerID());
                selectedCustomerLabel.setText(selectedCustomer[0].getCustomerID());
                isUpdating[0] = false;
            }
        });

        customerView.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                isUpdating[0] = true;
                
                selectedCustomer[0] = newVal;
                customerField.setText(newVal.getCustomerID());
                selectedCustomerLabel.setText(newVal.getCustomerID() + " | " + newVal.getName());

                selectedRoom[0] = hotelService.getRoomService().getRoom(newVal.getCurrStay().getRoomNumber());
                roomField.setText(Integer.toString(newVal.getCurrStay().getRoomNumber()));
                selectedRoomLabel.setText("Room " + newVal.getCurrStay().getRoomNumber() + " | " + selectedRoom[0].getRoomType().name());
                isUpdating[0] = false;
            }
        });


        roomField.textProperty().addListener((obs, old, newVal) -> {
            if (isUpdating[0]) return;
            if (newVal == null || newVal.trim().isEmpty()) {
                roomItems.clear();
                selectedRoom[0] = null;
                selectedRoomLabel.setText("");
            } else {
                ArrayList<Room> occupied = hotelService.getRoomService().getOccupiedRooms();
                occupied.removeIf(r -> !String.valueOf(r.getRoomNo()).contains(newVal));
                roomItems.setAll(occupied);
            }
        });

        if (checkOutCustomr != null) {
            selectedCustomer[0] = checkOutCustomr;
            customerField.setText(checkOutCustomr.getCustomerID());
            selectedCustomerLabel.setText(checkOutCustomr.getCustomerID() + " | " + checkOutCustomr.getName());
        }
        if (checkOutRoom != null) {
            selectedRoom[0] = checkOutRoom;
            roomField.setText(String.valueOf(checkOutRoom.getRoomNo()));
            selectedRoomLabel.setText("Room " + checkOutRoom.getRoomNo() + " | " + checkOutRoom.getRoomType());
        }

        Label statusLabel = new Label("");
        statusLabel.getStyleClass().add("label");

        Button checkOutBtn = new Button("Confirm Check Out");
        checkOutBtn.getStyleClass().add("action-button");
        checkOutBtn.setMaxWidth(Double.MAX_VALUE);

        checkOutBtn.setOnAction(e -> {
            statusLabel.getStyleClass().removeAll("status-success", "status-error");
            
            if (selectedCustomer[0] == null) {
                statusLabel.setText("Please select a customer");
                statusLabel.getStyleClass().add("status-error");
                return;
            }
            
            if (selectedRoom[0] == null) {
                statusLabel.setText("Please select a room");
                statusLabel.getStyleClass().add("status-error");
                return;
            }
            
            if (!(selectedCustomer[0].getCustomerID().equals(selectedRoom[0].returnCust().getCustomerID()))) {
                statusLabel.setText("Customer is not in the selected room");
                statusLabel.getStyleClass().add("status-error");
                return;
            }

            try {
                String custID = selectedCustomer[0].getCustomerID();

                this.hotelService.getCustomerService().customerCheckOut(custID);

                statusLabel.setText("Check-out successful for Room " + selectedRoom[0].getRoomNo());
                statusLabel.getStyleClass().add("status-success");

                customerField.clear(); 
                customerItems.clear(); 
                selectedCustomerLabel.setText("");
                selectedCustomer[0] = null;
                
                roomField.clear(); 
                roomItems.clear(); 
                selectedRoomLabel.setText("");
                selectedRoom[0] = null;

            } catch (Exception ex) {
                statusLabel.setText(ex.getMessage());
                statusLabel.getStyleClass().add("status-error");
            }
        });

        Button proceedToPayButton = new Button("Proceed to Payment");
        proceedToPayButton.getStyleClass().add("action-button");
        proceedToPayButton.setMaxWidth(Double.MAX_VALUE);

        proceedToPayButton.setOnAction(e -> {
            this.closingAnimation(animationLayer, scene, () -> {this.setUpPaymentProcessing();}, "#090201");
        });

        checkOutForm.getChildren().addAll(
            title, 
            customerTitle, customerField, customerView, selectedCustomerLabel,
            roomTitle, roomField, roomView, selectedRoomLabel,
            statusLabel, checkOutBtn, proceedToPayButton
        );

        Button backButton = new Button("Home", new ImageView(new Image(getClass().getResourceAsStream("/resource/home.png"))));
        ((ImageView)backButton.getGraphic()).setFitWidth(30);
        ((ImageView)backButton.getGraphic()).setPreserveRatio(true);
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(event -> this.closingAnimation(animationLayer, scene, () -> this.setUpHome(), "#090201"));

        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(20));

        contentLayer.getChildren().addAll(backButton, checkOutForm);
    }
  
    public void setUpPaymentProcessing() {
        contentLayer.getChildren().clear();
        contentLayer.getStyleClass().remove("home-background");
        contentLayer.getStyleClass().add("dashboard-background");

        VBox paymentForm = new VBox(15);
        paymentForm.setAlignment(Pos.CENTER);
        paymentForm.setMaxWidth(400);
        StackPane.setAlignment(paymentForm, Pos.CENTER);

        Label title = new Label("Process Payment & Finalize");
        title.getStyleClass().add("label");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");


        model.Customer[] selectedCustomer = {null};
        boolean[] isUpdating = {false};


        Label searchTitle = new Label("Find Pending Bill (Search by Name or ID)");
        searchTitle.getStyleClass().add("label");
        searchTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        TextField customerField = new TextField();
        customerField.setPromptText("Search customers with pending payments...");
        customerField.getStyleClass().add("search-input");

        ObservableList<model.Customer> customerItems = FXCollections.observableArrayList();
        customerItems.addAll(this.hotelService.getCustomerService().getPendingPayments());
        ListView<model.Customer> customerView = new ListView<>(customerItems);
        customerView.getStyleClass().add("results-area");
        customerView.setPrefHeight(120);


        VBox billDetailsBox = new VBox(5);
        billDetailsBox.setAlignment(Pos.CENTER);
        billDetailsBox.setStyle("-fx-background-color: rgba(0,0,0,0.2); -fx-padding: 10; -fx-background-radius: 5;");
        
        Label selectedCustomerLabel = new Label("No customer selected");
        selectedCustomerLabel.getStyleClass().add("label");
        selectedCustomerLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        Label roomInfoLabel = new Label();
        roomInfoLabel.getStyleClass().add("label");
        roomInfoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #aaa;");

        Label amountDueLabel = new Label("Amount Due: ₹0.00");
        amountDueLabel.getStyleClass().add("label");
        amountDueLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #e69191; -fx-font-weight: bold;"); 

        billDetailsBox.getChildren().addAll(selectedCustomerLabel, roomInfoLabel, amountDueLabel);

        customerView.setCellFactory(param -> new ListCell<model.Customer>() {
            protected void updateItem(model.Customer item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item.getCustomerID() + " - " + item.getName());
                    setStyle("-fx-text-fill: #dfc8a9; -fx-background-color: transparent; -fx-padding: 5;");
                }
            }
        });

        customerField.textProperty().addListener((obs, old, newVal) -> {
            if (isUpdating[0]) return;
            
            if (newVal == null || newVal.trim().isEmpty()) {
                customerItems.clear();
                selectedCustomer[0] = null;
                selectedCustomerLabel.setText("No customer selected");
                roomInfoLabel.setText("");
                amountDueLabel.setText("Amount Due: ₹0.00");
            } else {
                ArrayList<model.Customer> pendingCustomers = hotelService.getCustomerService().getPendingPayments();
                pendingCustomers.removeIf(c -> !c.getCustomerID().toLowerCase().contains(newVal.toLowerCase()) &&
                                            !c.getName().toLowerCase().contains(newVal.toLowerCase()));
                customerItems.setAll(pendingCustomers);
            }
        });

        customerView.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                isUpdating[0] = true;
                
                selectedCustomer[0] = newVal;
                customerField.setText(newVal.getCustomerID());
                selectedCustomerLabel.setText(newVal.getName() + " | " + newVal.getCustomerID());
                
                try {
                    int roomNo = newVal.getCurrStay().getRoomNumber();
                    model.Room room = hotelService.getRoomService().getRoom(roomNo);
                    roomInfoLabel.setText("Room " + roomNo + " (" + room.getRoomType().name() + ")");

                    java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern(model.TimeInfo.dateStoreFormat);
                    String checkInStr = newVal.getCurrStay().getTimeRecord().getCheckInDate();
                    String checkOutStr = newVal.getCurrStay().getTimeRecord().getCheckOutDate();
                    
                    LocalDate checkIn = (checkInStr != null && !checkInStr.isEmpty()) ? LocalDate.parse(checkInStr, fmt) : LocalDate.now();
                    LocalDate checkOut = (checkOutStr != null && !checkOutStr.isEmpty()) ? LocalDate.parse(checkOutStr, fmt) : LocalDate.now();
                    
                    long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
                    if (nights == 0) nights = 1;

                    double amountDue = hotelService.getBillingService().getBillAmount(room, (int) nights);
                    amountDueLabel.setText(String.format("Amount Due: ₹%.2f", amountDue));

                } catch (Exception ex) {
                    roomInfoLabel.setText("Room data unavailable");
                    amountDueLabel.setText("Amount Due: Error calculating");
                }
                
                isUpdating[0] = false;
            }
        });

        Label statusLabel = new Label("");
        statusLabel.getStyleClass().add("label");

        Button payBtn = new Button("Confirm Payment & Finalize Stay");
        payBtn.getStyleClass().add("action-button");
        payBtn.setMaxWidth(Double.MAX_VALUE);

        payBtn.setOnAction(e -> {
            statusLabel.getStyleClass().removeAll("status-success", "status-error");
            
            if (selectedCustomer[0] == null) {
                statusLabel.setText("Please select a customer with a pending bill.");
                statusLabel.getStyleClass().add("status-error");
                return;
            }

            try {
                String custID = selectedCustomer[0].getCustomerID();
                
                hotelService.getCustomerService().customerPayment(custID);
                hotelService.getCustomerService().customerFinalizeStay(custID);
                
                statusLabel.setText("Payment successful! Stay finalized for " + custID);
                statusLabel.getStyleClass().add("status-success");
                
                amountDueLabel.setText("PAID");
                amountDueLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #c1e884; -fx-font-weight: bold;"); 

                isUpdating[0] = true;
                customerField.clear();
                customerItems.clear();
                selectedCustomer[0] = null;
                isUpdating[0] = false;

            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
                statusLabel.getStyleClass().add("status-error");
            }
        });

        paymentForm.getChildren().addAll(
            title, 
            searchTitle, customerField, customerView, 
            billDetailsBox,
            statusLabel, payBtn
        );

        Button backButton = new Button("Home", new ImageView(new Image(getClass().getResourceAsStream("/resource/home.png"))));
        ((ImageView)backButton.getGraphic()).setFitWidth(30);
        ((ImageView)backButton.getGraphic()).setPreserveRatio(true);
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(event -> this.closingAnimation(animationLayer, scene, () -> this.setUpHome(), "#090201"));

        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(20));

        contentLayer.getChildren().addAll(backButton, paymentForm);
    }

    public void setUpRecordsViewing() {
        contentLayer.getChildren().clear();
        contentLayer.getStyleClass().remove("home-background");
        contentLayer.getStyleClass().add("dashboard-background");


        HBox mainLayout = new HBox(40);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setMaxWidth(850);
        StackPane.setAlignment(mainLayout, Pos.CENTER);

        VBox listPanel = new VBox(15);
        listPanel.setPrefWidth(350);
        
        Label title = new Label("Stay Records");
        title.getStyleClass().add("label");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #dfc8a9;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search Customer ID or Room No...");
        searchField.getStyleClass().add("search-input");

        ObservableList<model.StayRecord> recordItems = FXCollections.observableArrayList();
        
        ArrayList<model.StayRecord> allRecords = hotelService.getCustomerService().getAllRecords();
        allRecords.sort((r1, r2) -> {
            java.time.LocalDateTime t1 = r1.getTimeRecord().getCheckInLocalDateTime();
            java.time.LocalDateTime t2 = r2.getTimeRecord().getCheckInLocalDateTime();
            if (t1 == null && t2 == null) return 0;
            if (t1 == null) return 1;
            if (t2 == null) return -1;
            return t2.compareTo(t1);
        });
        recordItems.addAll(allRecords);

        ListView<model.StayRecord> recordsView = new ListView<>(recordItems);
        recordsView.getStyleClass().add("results-area");
        recordsView.setPrefHeight(400);

        recordsView.setCellFactory(param -> new ListCell<model.StayRecord>() {
            protected void updateItem(model.StayRecord item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    String date = item.getTimeRecord().getCheckInDate();
                    setText("Room " + item.getRoomNumber() + " | " + item.getCustomerID() + "\n" + date);
                    setStyle("-fx-text-fill: #dfc8a9; -fx-background-color: transparent; -fx-padding: 8;");
                }
            }
        });

        searchField.textProperty().addListener((obs, old, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                recordItems.setAll(allRecords);
            } else {
                String query = newVal.toLowerCase().trim();
                ArrayList<model.StayRecord> filtered = new ArrayList<>();
                for (model.StayRecord r : allRecords) {
                    if (r.getCustomerID().toLowerCase().contains(query) || 
                        String.valueOf(r.getRoomNumber()).contains(query)) {
                        filtered.add(r);
                    }
                }
                recordItems.setAll(filtered);
            }
        });

        listPanel.getChildren().addAll(title, searchField, recordsView);

        VBox detailsPanel = new VBox(15);
        detailsPanel.setPrefWidth(400);
        detailsPanel.getStyleClass().add("searcher-box"); 
        detailsPanel.setAlignment(Pos.TOP_LEFT);

        Label detailsTitle = new Label("Record Details");
        detailsTitle.getStyleClass().add("searcher-header");
        detailsTitle.setStyle("-fx-font-size: 18px;");

        VBox infoContainer = new VBox(12);
        
        Label emptyLabel = new Label("Select a record from the list to view full details.");
        emptyLabel.setStyle("-fx-text-fill: rgba(223, 200, 169, 0.5); -fx-font-style: italic;");
        infoContainer.getChildren().add(emptyLabel);

        recordsView.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            infoContainer.getChildren().clear();
            if (newVal != null) {

                Label separator1 = new Label("--------------------------------------------------");
                separator1.setStyle("-fx-text-fill: #794F1F;");
                Label separator2 = new Label("--------------------------------------------------");
                separator2.setStyle("-fx-text-fill: #794F1F;");

                infoContainer.getChildren().addAll(
                    createDetailRow("Record ID:", newVal.getRecordID()),
                    createDetailRow("Customer ID:", newVal.getCustomerID()),
                    createDetailRow("Room No:", String.valueOf(newVal.getRoomNumber())),
                    
                    separator1,
                    
                    createDetailRow("Check-In:", newVal.getTimeRecord().getCheckInInfo(false)),
                    createDetailRow("Check-Out:", newVal.getTimeRecord().getCheckOutInfo(false)),
                    
                    separator2,
                    
                    createDetailRow("Payment ID:", newVal.getPaymentRecord().getPaymentID()),
                    createDetailRow("Amount Paid:", "₹" + String.format("%.2f", newVal.getPaymentAmt())),
                    createDetailRow("Method:", newVal.getPaymentType() != null ? newVal.getPaymentType().getName() : "Pending"),
                    createDetailRow("Status:", newVal.getPaymentStatus() != null ? newVal.getPaymentStatus().getName() : "Pending")
                );
            } else {
                infoContainer.getChildren().add(emptyLabel);
            }
        });

        detailsPanel.getChildren().addAll(detailsTitle, infoContainer);

        mainLayout.getChildren().addAll(listPanel, detailsPanel);

        Button backButton = new Button("Home", new ImageView(new Image(getClass().getResourceAsStream("/resource/home.png"))));
        ((ImageView)backButton.getGraphic()).setFitWidth(30);
        ((ImageView)backButton.getGraphic()).setPreserveRatio(true);
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(event -> this.closingAnimation(animationLayer, scene, () -> this.setUpHome(), "#090201"));

        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(20));

        contentLayer.getChildren().addAll(backButton, mainLayout);
    }

    private HBox createDetailRow(String labelText, String valueText) {
        HBox row = new HBox(10);
        
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-text-fill: #dfc8a9; -fx-font-weight: bold; -fx-min-width: 100px;");
        
        Label val = new Label(valueText != null && !valueText.isEmpty() ? valueText : "N/A");
        val.setStyle("-fx-text-fill: white; -fx-wrap-text: true;");
        
        row.getChildren().addAll(lbl, val);
        return row;
    }
    
    public VBox createHomeCard(String title, Node icon, Runnable onClickAction){
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("home-card");  
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        card.getChildren().addAll(icon, titleLabel);

        card.setOnMouseClicked(event -> {
            onClickAction.run();
        });

        return card;
    }

    public void expandingAnimation(VBox clickedCard, Pane animationLayer, Scene scene, Runnable onFinished, String transitionColorCSS, String targetColorS, String currentColor){
        Bounds bounds = clickedCard.localToScene(clickedCard.getBoundsInLocal());
        Interpolator easeOutCircle = Interpolator.SPLINE(0.33, 1, 0.68, 1);

        ImageView originalIcon = (ImageView) clickedCard.getChildren().get(0);
        ImageView proxyIcon = new ImageView(originalIcon.getImage());

        proxyIcon.setFitWidth(originalIcon.getFitWidth());
        proxyIcon.setFitHeight(originalIcon.getFitHeight());
        proxyIcon.setPreserveRatio(true);

        Label originalLabel = (Label) clickedCard.getChildren().get(1);
        Label proxyLabel = new Label(originalLabel.getText());
        proxyLabel.getStyleClass().add("label_copy");

        Pane overlay2= new Pane();
        overlay2.setPrefSize(scene.getWidth(), scene.getHeight());
        overlay2.getStyleClass().add("home-background");
        overlay2.setOpacity(0);

        Circle overlay1 = new Circle();
        overlay1.setCenterX(scene.getWidth() / 2);
        overlay1.setCenterY(scene.getHeight() / 2);
        overlay1.setStyle(String.format("-fx-fill: %s;", transitionColorCSS));
        overlay1.setOpacity(1);
        
        animationLayer.getChildren().add(overlay2);
        animationLayer.getChildren().add(overlay1);

        StackPane fakeCard = new StackPane();
        fakeCard.getChildren().add(proxyIcon);
        fakeCard.getChildren().add(proxyLabel);
        
        fakeCard.getStyleClass().add("home-card");

        fakeCard.setLayoutX(bounds.getMinX());
        fakeCard.setLayoutY(bounds.getMinY());
        fakeCard.setPrefWidth(bounds.getWidth());
        fakeCard.setPrefHeight(bounds.getHeight());

        double iconHeight = proxyIcon.getBoundsInLocal().getHeight(); 
        double labelHeight = proxyLabel.getBoundsInLocal().getHeight();
        double spacing = 26.0;

        StackPane.setMargin(proxyIcon, new Insets(0, 0, labelHeight + spacing, 0));
        StackPane.setMargin(proxyLabel, new Insets(iconHeight + spacing, 0, 0, 0));
        fakeCard.setAlignment(Pos.CENTER);
        fakeCard.setEffect(null);

        animationLayer.getChildren().add(fakeCard);

        Timeline timeline1 = new Timeline();
        KeyValue kvX1 = new KeyValue(fakeCard.layoutXProperty(), scene.getWidth()/2 - bounds.getWidth()/2, Interpolator.EASE_IN);
        KeyValue kvY1 = new KeyValue(fakeCard.layoutYProperty(), scene.getHeight()/2 - bounds.getHeight()/2, Interpolator.EASE_IN);
        ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(Color.web(currentColor));
        colorProperty.addListener((obs, oldColor, newColor) -> {
            String hex = String.format("#%02X%02X%02X", 
                (int)(newColor.getRed() * 255), 
                (int)(newColor.getGreen() * 255), 
                (int)(newColor.getBlue() * 255));
            
            fakeCard.setStyle("-fx-background-color: " + hex + "; -fx-background-radius: 0; -fx-effect: null;");
        });
        
        Color targetColor = Color.web(targetColorS);
        KeyValue kvColor = new KeyValue(colorProperty, targetColor, Interpolator.EASE_BOTH);
        KeyFrame kf1 = new KeyFrame(Duration.seconds(0.2), kvX1, kvY1, kvColor);

        
        timeline1.getKeyFrames().add(kf1);


        Timeline timeline2 = new Timeline();
        double finalRadius = Math.sqrt((Math.pow(scene.getHeight()/2, 2) + Math.pow(scene.getWidth()/2, 2)));
        KeyValue kvRadius = new KeyValue(overlay1.radiusProperty(), finalRadius, easeOutCircle);
        KeyValue kvOpacity = new KeyValue(fakeCard.opacityProperty(), 0, Interpolator.EASE_BOTH);

        
        KeyFrame kf2 = new KeyFrame(Duration.seconds(0.25), kvRadius);
        KeyFrame kf3 = new KeyFrame(Duration.seconds(0.35), kvOpacity);
        timeline2.getKeyFrames().add(kf2);
        timeline2.getKeyFrames().add(kf3);

        Timeline timeline3 = new Timeline();
        KeyValue kvOpacity2 = new KeyValue(overlay1.opacityProperty(), 0, Interpolator.EASE_BOTH);
        KeyFrame kf4 = new KeyFrame(Duration.seconds(0.1), kvOpacity2);

        timeline3.getKeyFrames().add(kf4);

        timeline2.setOnFinished(event -> {
            onFinished.run(); 
            animationLayer.getChildren().remove(fakeCard);
            animationLayer.getChildren().remove(overlay2);
            timeline3.play();
        });

        timeline3.setOnFinished(event -> {
            animationLayer.getChildren().remove(overlay1);
        });

        timeline1.setOnFinished( e -> {
            timeline2.play();
        });

        overlay2.setOpacity(1);
        timeline1.play();
    }

    public void closingAnimation(Pane animationLayer, Scene scene, Runnable onFinished, String targetColorHex) {
        Circle overlay = new Circle();
        overlay.setCenterX(50); 
        overlay.setCenterY(50);
        overlay.setRadius(0);
        overlay.setStyle("-fx-fill: " + targetColorHex + ";");
        
        animationLayer.getChildren().add(overlay);
        
        double finalRadius = Math.sqrt(Math.pow(scene.getWidth(), 2) + Math.pow(scene.getHeight(), 2));
        
        Timeline expand = new Timeline();
        KeyValue kvRadius = new KeyValue(overlay.radiusProperty(), finalRadius, Interpolator.EASE_BOTH);
        KeyFrame kf = new KeyFrame(Duration.seconds(0.4), kvRadius);
        expand.getKeyFrames().add(kf);
        
        expand.setOnFinished(event -> {
            onFinished.run();
            
            Timeline fadeOut = new Timeline(new KeyFrame(Duration.seconds(0.3), new KeyValue(overlay.opacityProperty(), 0)));
            fadeOut.setOnFinished(e -> animationLayer.getChildren().remove(overlay));
            fadeOut.play();
        });
        
        expand.play();
    }

    private VBox createCustomerSearcher() {
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name or ID...");
        searchField.getStyleClass().add("search-input");

        ObservableList<model.Customer> items = FXCollections.observableArrayList();
        items.addAll(hotelService.getCustomerService().getAllCustomers());
        ListView<model.Customer> resultsArea = new ListView<>(items);
        resultsArea.getStyleClass().add("results-area");

        resultsArea.setCellFactory(param -> {
            ListCell<model.Customer> cell = new ListCell<>() {
                protected void updateItem(model.Customer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("-fx-background-color: transparent;");
                    } else {
                        setText(item.getCustomerID() + " - " + item.getName());
                        if (isSelected()) {
                            setStyle("-fx-background-color: #4D2C1D; -fx-text-fill: #dfc8a9; -fx-padding: 5; -fx-background-radius: 5;");
                        } else {
                            setStyle("-fx-background-color: transparent; -fx-text-fill: #dfc8a9; -fx-padding: 5;");
                        }
                    }
                }
            };
            
            cell.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (cell.getItem() != null) {
                    if (isNowSelected) {
                        cell.setStyle("-fx-background-color: #4D2C1D; -fx-text-fill: #dfc8a9; -fx-padding: 5; -fx-background-radius: 5;");
                    } else {
                        cell.setStyle("-fx-background-color: transparent; -fx-text-fill: #dfc8a9; -fx-padding: 5;");
                    }
                }
            });
            return cell;
        });

        searchField.textProperty().addListener((obs, old, newVal) -> {
            try {
                if (newVal == null || newVal.trim().isEmpty()) {
                    ArrayList<model.Customer> results = hotelService.getCustomerService().getAllCustomers();
                    items.setAll(results);
                } else {
                    ArrayList<model.Customer> results = hotelService.getCustomerService().queryCustomers(
                        c -> c.getCustomerID().toLowerCase().contains(newVal.toLowerCase())
                    );
                    if(results.isEmpty()) {
                        results.addAll(hotelService.getCustomerService().queryCustomers(
                            c -> c.getName().toLowerCase().contains(newVal.toLowerCase())
                        ));
                    }
                    items.setAll(results);
                }
            } catch (Exception e) {
                System.err.println("Customer search error: " + e.getMessage());
                items.clear();
            }
        });

        Button registerBtn = new Button("Register");
        registerBtn.getStyleClass().add("action-button");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setOnAction(event -> {
            this.closingAnimation(animationLayer, scene, () -> {this.setUpCustomerRegistration();}, "#090201");
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: #090201; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand;");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setDisable(true); 

        resultsArea.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            deleteBtn.setDisable(newVal == null);
        });

        deleteBtn.setOnAction(e -> {
            model.Customer selected = resultsArea.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirm Deletion");
                confirm.setHeaderText("Delete " + selected.getName() + "?");
                confirm.setContentText("Are you sure you want to delete this customer? This cannot be undone.");

                styleAlert(confirm);
                
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            hotelService.getCustomerService().unregisterCustomer(selected.getCustomerID());
                            setUpDashboard(); 
                        } catch (Exception ex) {
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                            styleAlert(errorAlert);
                            errorAlert.show();
                        }
                    }
                });
            }
        });

        HBox actionBox = new HBox(10, registerBtn, deleteBtn);
        actionBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(registerBtn, Priority.ALWAYS);
        HBox.setHgrow(deleteBtn, Priority.ALWAYS);

        return createBaseSearcherBox("CUSTOMER SEARCH", searchField, resultsArea, actionBox);
    }

    private VBox createBaseSearcherBox(String title, Region searchLayout, ListView<?> resultsArea, Node actionNode) {
        VBox box = new VBox(15);
        box.setAlignment(Pos.TOP_CENTER);
        box.getStyleClass().add("searcher-box");
        
        box.prefHeightProperty().bind(contentLayer.heightProperty().subtract(60));
        box.setPrefWidth(280); 

        Text header = new Text(title);
        header.getStyleClass().add("searcher-header");

        VBox.setVgrow(resultsArea, Priority.ALWAYS);

        box.getChildren().addAll(header, searchLayout, resultsArea, actionNode);
        return box;
    }

    private VBox createRoomSearcher() {
        HBox searchLayout = new HBox(5);
        
        TextField searchField = new TextField();
        searchField.setPromptText("Search by room number...");
        searchField.getStyleClass().add("search-input");
        HBox.setHgrow(searchField, Priority.ALWAYS); 
        
        MenuButton filterBtn = new MenuButton("Filters");
        filterBtn.getStyleClass().add("filter-button");

        ArrayList<model.roomUtil.Amenities> selectedAmenities = new ArrayList<>();
        ObservableList<model.Room> items = FXCollections.observableArrayList();
        items.addAll(hotelService.getRoomService().getAllRooms());

        model.roomUtil.Amenities[] options = {
            model.Room.createWifiAmenity(), 
            model.Room.createACAmenity(),
            model.Room.createAttachedBathroom(),
            model.Room.createSeaViewAmenity("Default")
        };
        
        ToggleGroup occupancyGroup = new ToggleGroup();
        
        for (model.roomUtil.Amenities amenity : options) {
            CheckMenuItem checkItem = new CheckMenuItem(amenity.returnName());
            checkItem.selectedProperty().addListener((obs, old, isSelected) -> {
                if (isSelected) selectedAmenities.add(amenity);
                else selectedAmenities.remove(amenity);
                
                String occState = occupancyGroup.getSelectedToggle().getUserData().toString();
                updateRoomResults(searchField.getText(), items, selectedAmenities, occState);
            });
            filterBtn.getItems().add(checkItem);
        }

        filterBtn.getItems().add(new SeparatorMenuItem());

        RadioMenuItem allItem = new RadioMenuItem("All Rooms");
        allItem.setUserData("ALL");
        allItem.setSelected(true);
        allItem.setToggleGroup(occupancyGroup);

        RadioMenuItem availableItem = new RadioMenuItem("Available Only");
        availableItem.setUserData("AVAILABLE");
        availableItem.setToggleGroup(occupancyGroup);

        RadioMenuItem occupiedItem = new RadioMenuItem("Occupied Only");
        occupiedItem.setUserData("OCCUPIED");
        occupiedItem.setToggleGroup(occupancyGroup);

        filterBtn.getItems().addAll(allItem, availableItem, occupiedItem);

        occupancyGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateRoomResults(searchField.getText(), items, selectedAmenities, newVal.getUserData().toString());
            }
        });

        searchLayout.getChildren().addAll(searchField, filterBtn);

        ListView<model.Room> resultsArea = new ListView<>(items);
        resultsArea.getStyleClass().add("results-area");

        resultsArea.setCellFactory(param -> {
            ListCell<model.Room> cell = new ListCell<>() {
                protected void updateItem(model.Room item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("-fx-background-color: transparent;");
                    } else {
                        String status = item.getFlag(true) ? " (Occupied)" : " (Available)";
                        setText("Room - " + item.getRoomNo() + status);
                        if (isSelected()) {
                            setStyle("-fx-background-color: #4D2C1D; -fx-text-fill: #dfc8a9; -fx-padding: 5; -fx-background-radius: 5;");
                        } else {
                            setStyle("-fx-background-color: transparent; -fx-text-fill: #dfc8a9; -fx-padding: 5;");
                        }
                    }
                }
            };
            
            cell.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (cell.getItem() != null) {
                    if (isNowSelected) {
                        cell.setStyle("-fx-background-color: #4D2C1D; -fx-text-fill: #dfc8a9; -fx-padding: 5; -fx-background-radius: 5;");
                    } else {
                        cell.setStyle("-fx-background-color: transparent; -fx-text-fill: #dfc8a9; -fx-padding: 5;");
                    }
                }
            });
            return cell;
        });

        searchField.textProperty().addListener((obs, old, newVal) -> {
            String occState = occupancyGroup.getSelectedToggle().getUserData().toString();
            updateRoomResults(newVal, items, selectedAmenities, occState);
        });

        Button registerBtn = new Button("Register");
        registerBtn.getStyleClass().add("action-button");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setOnAction(event -> {
            this.closingAnimation(animationLayer, scene, () -> {this.setUpRoomCreation();}, "#090201");
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: #090201; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand;");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setDisable(true); 

        resultsArea.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            deleteBtn.setDisable(newVal == null);
        });

        deleteBtn.setOnAction(e -> {
            model.Room selected = resultsArea.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirm Deletion");
                confirm.setHeaderText("Delete Room " + selected.getRoomNo() + "?");
                confirm.setContentText("Are you sure you want to delete this room? This cannot be undone.");
                
                styleAlert(confirm);
                
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            hotelService.getRoomService().removeRoom(selected.getRoomNo());
                            setUpDashboard(); 
                        } catch (Exception ex) {
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                            styleAlert(errorAlert);
                            errorAlert.show();
                        }
                    }
                });
            }
        });

        HBox actionBox = new HBox(10, registerBtn, deleteBtn);
        actionBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(registerBtn, Priority.ALWAYS);
        HBox.setHgrow(deleteBtn, Priority.ALWAYS);

        return createBaseSearcherBox("ROOM SEARCH", searchLayout, resultsArea, actionBox);
    }

    private void updateRoomResults(String query, ObservableList<Room> items, ArrayList<model.roomUtil.Amenities> amenities, String occupancyStatus) {
        try {
            ArrayList<model.Room> baseRooms;

            if (amenities != null && !amenities.isEmpty()) {
                baseRooms = hotelService.getRoomService().getRoomsWithAmenity(amenities);
            } else {
                baseRooms = hotelService.getRoomService().getAllRooms();
            }

            baseRooms.removeIf(r -> {
                boolean isOccupied = r.getFlag(true);
                if (occupancyStatus.equals("AVAILABLE")) return isOccupied; // Remove if it IS occupied
                if (occupancyStatus.equals("OCCUPIED")) return !isOccupied; // Remove if it IS NOT occupied
                return false; // "ALL" keeps everything
            });


            String searchStr = (query != null) ? query.trim() : "";
            if (!searchStr.isEmpty()) {
                baseRooms.removeIf(r -> !Integer.toString(r.getRoomNo()).contains(searchStr));
            }

            items.setAll(baseRooms);
        } catch (Exception e) {
            System.err.println("Room search error: " + e.getMessage());
            items.clear(); 
        }
    }

    private void styleAlert(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        try {
            dialogPane.getStylesheets().add(getClass().getResource("/resource/style.css").toExternalForm());
        } catch (Exception ignored) {}

        dialogPane.setStyle("-fx-background-color: #210804; -fx-border-color: #dfc8a9; -fx-border-width: 2px;");
        
        Node content = dialogPane.lookup(".content.label");
        if (content != null) content.setStyle("-fx-text-fill: #dfc8a9; -fx-font-size: 14px;");
      
        Node header = dialogPane.lookup(".header-panel");
        if (header != null) header.setStyle("-fx-background-color: #090201;");
        
        Node headerText = dialogPane.lookup(".header-panel .label");
        if (headerText != null) headerText.setStyle("-fx-text-fill: #dfc8a9; -fx-font-size: 18px; -fx-font-weight: bold;");
    }
}

