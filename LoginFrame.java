import javax.swing.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class User {
    private String id;
    private String name;
    private String username;
    private String password;

    public User(String id, String name, String username, String password) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

class Product {
    private String productNumber;
    private String productName;
    private String description;
    private int quantity;
    private double price;

    public Product(String productNumber, String productName, String description, int quantity, double price) {
        this.productNumber = productNumber;
        this.productName = productName;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public String getProductName() {
        return productName;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Product Number: " + productNumber + "\n" +
                "Product Name: " + productName + "\n" +
                "Description: " + description + "\n" +
                "Quantity: " + quantity + "\n" +
                "Price: $" + price + "\n";
    }
}

class AddProductFrame extends JFrame implements ActionListener {
    private JTextField productNumberField;
    private JTextField productNameField;
    private JTextField descriptionField;
    private JTextField quantityField;
    private JTextField priceField;
    private JButton addButton;
//     private JButton confirmButton;
    private JTextArea productListArea;
    private JScrollPane scrollPane;
    private User registeredUser;
    private List<Product> productList;
    private JButton proceedButton;
    
    public double getTotalPrice() {
        double total = 0;
        for (Product product : productList) {
            total += product.getPrice() * product.getQuantity();
        }
        return total;
    }

    public AddProductFrame(User registeredUser) {
        super("Add Product");

        this.registeredUser = registeredUser;
        productList = new ArrayList<>();

        // Create components
        productNumberField = new JTextField();
        productNameField = new JTextField();
        descriptionField = new JTextField();
        quantityField = new JTextField();
        priceField = new JTextField();
        addButton = new JButton("Add Product");
        proceedButton = new JButton("Proceed to Buy");
        productListArea = new JTextArea(20, 40);
        productListArea.setEditable(false);
        scrollPane = new JScrollPane(productListArea);

        // Set layouts
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // Add components to frame
        add(new JLabel("User ID: " + registeredUser.getId()));
        add(new JLabel("User Name: " + registeredUser.getName()));
        add(new JLabel("Product Number:"));
        add(productNumberField);
        add(new JLabel("Product Name:"));
        add(productNameField);
        add(new JLabel("Description:"));
        add(descriptionField);
        add(new JLabel("Quantity:"));
        add(quantityField);
        add(new JLabel("Price:"));
        add(priceField);
        add(addButton);
        add(proceedButton); 
        add(scrollPane);

        // Add action listeners
        addButton.addActionListener(this);
        proceedButton.addActionListener(this);

        // Set frame properties
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            try {
                String productNumber = productNumberField.getText();
                String productName = productNameField.getText();
                String description = descriptionField.getText();
                int quantity = Integer.parseInt(quantityField.getText());
                double price = Double.parseDouble(priceField.getText());

                Product newProduct = new Product(productNumber, productName, description, quantity, price);
                productList.add(newProduct);

                updateProductListArea();
                clearFields();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric values for quantity and price.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == proceedButton) {
            openBuyProductsFrame(); // New method for handling the "Proceed" button
        }
    }
    
    private void openBuyProductsFrame() {
    BuyProductsFrame buyProductsFrame = new BuyProductsFrame(getProductList(), this);
    buyProductsFrame.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            // Handle window closing event if needed
        }
    });
}

    
    

    private void clearFields() {
        productNumberField.setText("");
        productNameField.setText("");
        descriptionField.setText("");
        quantityField.setText("");
        priceField.setText("");
    }

    void updateProductListArea() {
        productListArea.setText("");
        for (Product product : productList) {
            productListArea.append(product.toString() + "---------------\n");
        }
    }

    public List<Product> getProductList() {
        return productList;
    }
}

class BuyProductsFrame extends JFrame implements ActionListener {
    private List<Product> productList;
    private AddProductFrame addProductFrame;
    private List<JSpinner> quantitySpinners;
    private JTextField transactionIdField;
    private JTextField customerIdField;
    private JTextField totalPriceField;
    private JButton buyButton;
    private JButton generateIdButton;

    public BuyProductsFrame(List<Product> productList, AddProductFrame addProductFrame) {
        super("Buy Products");

        this.productList = productList;
        this.addProductFrame = addProductFrame;

        // Create components for selecting products and quantities
        quantitySpinners = new java.util.ArrayList<>();
        JPanel productPanel = new JPanel(new GridLayout(productList.size(), 2));

        for (Product product : productList) {
            JLabel productLabel = new JLabel(product.getProductName());
            JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, product.getQuantity(), 1));
            productPanel.add(productLabel);
            productPanel.add(quantitySpinner);
            quantitySpinners.add(quantitySpinner);
            
            quantitySpinner.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    updateTotalPrice();
                }
            });
        }
        // Create a random transaction ID
        String randomTransactionId = generateRandomTransactionId();
        
        // Initialize transactionIdField with the random transaction ID
        transactionIdField = new JTextField(randomTransactionId);
        transactionIdField.setEditable(false); // Make it non-editable
        
        // Create "Generate ID" button
        generateIdButton = new JButton("Generate ID");
        generateIdButton.addActionListener(this);
        
        transactionIdField = new JTextField();
        customerIdField = new JTextField();
        totalPriceField = new JTextField();
        totalPriceField.setEditable(false);

        // Create Buy button
        buyButton = new JButton("Buy");
        buyButton.addActionListener(this);

        // Add components to frame
        add(new JLabel("Products:"));
        add(productPanel);
        add(new JLabel("Transaction ID:"));
        add(transactionIdField);
        add(generateIdButton); // Add the "Generate ID" button
        add(new JLabel("Customer ID:"));
        add(customerIdField);
        add(new JLabel("Total Price:"));
        add(totalPriceField);
        add(buyButton);

        // Set layout
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // Set frame properties
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setVisible(true);

        updateTotalPrice(); // Calculate and display the initial total price
    }
    
    // Method to generate a random transaction ID
    private String generateRandomTransactionId() {
        UUID uuid = UUID.randomUUID();
        String randomTransactionId = uuid.toString().substring(0, 8); // Use the first 8 characters
        return randomTransactionId;
    }
    
    private void updateTotalPrice() {
        double total = 0;
        for (int i = 0; i < quantitySpinners.size(); i++) {
            int quantity = (int) quantitySpinners.get(i).getValue();
            total += productList.get(i).getPrice() * quantity;
        }
        totalPriceField.setText(String.valueOf(total));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    if (e.getSource() == generateIdButton) {
            // Generate and set a new random transaction ID
            String randomTransactionId = generateRandomTransactionId();
            transactionIdField.setText(randomTransactionId);
        } else if (e.getSource() == buyButton) {
            // Perform the buying logic here
            String transactionId = transactionIdField.getText();
            String customerId = customerIdField.getText();

            // Validate input
            if (transactionId.isEmpty() || customerId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter transaction details.", "Incomplete Details", JOptionPane.ERROR_MESSAGE);
            } else {
                // Deduct the quantity from the AddProductFrame for selected products
                for (int i = 0; i < quantitySpinners.size(); i++) {
                    int quantity = (int) quantitySpinners.get(i).getValue();
                    if (quantity > 0) {
                        deductQuantity(productList.get(i).getProductName(), quantity);
                    }
                }

                // Process the purchase (you can add your logic here)
                String purchaseDetails = "Purchase successful!\n";
                purchaseDetails += "Transaction ID: " + transactionId + "\n";
                purchaseDetails += "Customer ID: " + customerId + "\n";
                purchaseDetails += "Total Price: $" + totalPriceField.getText() + "\n";
                JOptionPane.showMessageDialog(this, purchaseDetails, "Purchase Complete", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the BuyProductsFrame after purchase
            }
        }
    }

    private void deductQuantity(String productName, int quantity) {
        for (Product product : productList) {
            if (product.getProductName().equals(productName)) {
                int currentQuantity = product.getQuantity();
                if (currentQuantity >= quantity) {
                    product.setQuantity(currentQuantity - quantity); // Deduct selected quantity
                    addProductFrame.updateProductListArea(); // Update the AddProductFrame
                    break;
                } else {
                    JOptionPane.showMessageDialog(this, "Not enough units available for " + productName, "Insufficient Stock", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }
}

class RegisterFrame extends JFrame implements ActionListener {
    private JTextField idField;
    private JTextField nameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton signInButton;
    private LoginFrame loginFrame;
    private List<User> registeredUsers;
    private User registeredUser;

    public RegisterFrame(LoginFrame loginFrame) {
        super("Register");

        this.loginFrame = loginFrame;

        // Create components
        idField = new JTextField();
        nameField = new JTextField();
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();
        registerButton = new JButton("Register");
        signInButton = new JButton("Sign-In");

        // Set layouts
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // Add components to frame
        add(new JLabel("ID:"));
        add(idField);
        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(new JLabel("Confirm Password:"));
        add(confirmPasswordField);
        add(registerButton);
        add(signInButton);

        // Add action listeners
        registerButton.addActionListener(this);
        signInButton.addActionListener(this);

        // Set frame properties
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 300);
        setLocationRelativeTo(null);
        setVisible(true);
        registeredUsers = new ArrayList<>();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String id = idField.getText();
        String name = nameField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (e.getSource() == registerButton) {
            if (validateInput(id, name, username, password, confirmPassword)) {
                if (registerUser(id, name, username, password)) {
                    registeredUser = new User(id, name, username, password);
                    loginFrame.addRegisteredUser(registeredUser);
                    JOptionPane.showMessageDialog(this, "Registration successful. You can now log in.");
                    dispose();
                    loginFrame.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid input. Please fill in all the fields.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == signInButton) {
            dispose();
            loginFrame.setVisible(true);
        }
    }

    private boolean validateInput(String id, String name, String username, String password, String confirmPassword) {
        return !id.isEmpty() && !name.isEmpty() && !username.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty();
    }

    private boolean registerUser(String id, String name, String username, String password) {
        if (!password.equals(confirmPasswordField.getText())) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Registration Unsuccessful", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        for (User user : registeredUsers) {
            if (user.getUsername().equals(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists.", "Registration Unsuccessful", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        User newUser = new User(id, name, username, password);
        registeredUsers.add(newUser);
        return true;
    }
}

public class LoginFrame extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private List<User> registeredUsers;
    private User loggedInUser;

    public LoginFrame() {
        super("Login System");

        registeredUsers = new ArrayList<>();

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(loginButton);
        add(registerButton);

        loginButton.addActionListener(this);
        registerButton.addActionListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);
        setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (e.getSource() == loginButton) {
            if (loginUser(username, password)) {
                loggedInUser = getRegisteredUserByUsername(username);
                JOptionPane.showMessageDialog(this, "Login successful. Welcome, " + username + "!");
                openAddProductFrame();
                setLoginFrameVisibility(false);
            } else {
                JOptionPane.showMessageDialog(this, "Login failed. Incorrect username or password.", "Login Unsuccessful", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == registerButton) {
            openRegisterFrame();
            setLoginFrameVisibility(false);
        }
    }
    
    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoginFrameVisibility(boolean visible) {
        setVisible(visible);
    }

    private void openAddProductFrame() {
        AddProductFrame addProductFrame = new AddProductFrame(getLoggedInUser());
        addProductFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                List<Product> productList = addProductFrame.getProductList();
                for (Product product : productList) {
                    System.out.println(product.toString() + "---------------");
                }
                System.exit(0);
            }
        });
    }
    
    private User getRegisteredUserByUsername(String username) {
        for (User user : registeredUsers) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null; // Return null if the user is not found
    }

    private boolean loginUser(String username, String password) {
        for (User user : registeredUsers) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    private void openRegisterFrame() {
        RegisterFrame registerFrame = new RegisterFrame(this);
        registerFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                // Handle window closing event if needed
            }
        });
    }

    public void addRegisteredUser(User newUser) {
        registeredUsers.add(newUser);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
