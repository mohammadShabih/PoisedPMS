import java.sql.*;
import java.time.LocalDate;
import java.util.Objects;
/**
 * The `Poised_ver2` class represents a simple project management system with basic SQL queries.
 * It interacts with the database poisepms, and the tables within it, project and person
 */
public class Poised_ver2 {

    //creating KeyboardInput variable
    static KeyboardInput instance  = KeyboardInput.getInstance();

    private static final String username = "root";

    private static final String password = "root";

    private static final String DB_url = "jdbc:mysql://localhost:3306/poisepms?useSSL=false";
    
    // main
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_url, username, password)){
            Statement statement = connection.createStatement();
            // creating and adding data to the tables if they do not exist within database
            if (!isTableExists(connection, "Person")){
                statement.executeUpdate("CREATE TABLE Person (person_ID INT PRIMARY KEY AUTO_INCREMENT," +
                        " name VARCHAR(150), surname VARCHAR(150), telNum VARCHAR(25) UNIQUE, email VARCHAR(150) UNIQUE," +
                        " phyAdress VARCHAR(200), occupation VARCHAR(50))");
                statement.executeUpdate("insert into poisepms.Person(name, surname, telNum, email, phyAdress, occupation) values" +
                        "('John', 'Doe', '1234567890', 'john@gmail.com', '123 Main St', 'Engineer')," +
                        "('Jane', 'Smith', '9876543210', 'jane@yahoo.com', '456 Oak Ave', 'Architect')," +
                        "('Bob','Johnson', '5551234567', 'bob@bestStore.com', '789 Pine Blvd', 'Contractor')," +
                        "('Mbasa','Zinja', '0572203578', 'mbasa@gmail.com', '123 Sub St', 'Engineer')," +
                        "('Gift','Ramhova', '0838225469', 'jane@gmail.com', '6 Poke Esate', 'Architect')," +
                        "('Vuyo','Sikuza', '078859738', 'vuyo@hotmail.com', '79 Asinine Blvd', 'Contractor')," +
                        "('Bulelani','Tokota', '0761235874', 'bulelani@gmail.com', '6 Pokentire Esate', 'Customer')," +
                        "('Michel','Du Plesis', '0785497842', 'mcihel@hotmail.com', '9 Bordeaux Blvd', 'Customer')");
            }

            if (!isTableExists(connection, "Project")){
                statement.executeUpdate("CREATE TABLE Project (proj_num INT PRIMARY KEY AUTO_INCREMENT," +
                        "proj_name VARCHAR(255) NOT NULL,proj_building VARCHAR(255),proj_address VARCHAR(255)," +
                        "proj_erf_number VARCHAR(50),total_expense INT,expense_paid INT,deadline DATE,complete BOOLEAN," +
                        "complete_date DATE, customer_id INT,architect_id INT,contractor_id INT,FOREIGN KEY (customer_id) REFERENCES Person(person_ID)," +
                        "FOREIGN KEY (architect_id) REFERENCES Person(person_ID)," +
                        "FOREIGN KEY (contractor_id) REFERENCES Person(person_ID)," +
                        "UNIQUE (proj_name, proj_building, proj_address, customer_id, architect_id, contractor_id))");
                statement.executeUpdate("insert into poisepms.Project(proj_name, proj_building, proj_address, proj_erf_number, total_expense," +
                        "expense_paid, deadline, complete, complete_date, customer_id, architect_id, contractor_id) values" +
                        "('Office Renovation', 'Building A', '23 Mandela Street', 'ABC123', 50000, 20000, '2025-01-01', false, null, 7, 2, 3)," +
                        "('Residential Development', 'Block B', '67 Oakwood Avenue', 'XYZ789', 150000, 80000, '2024-12-15', false, null, 8, 5, 6);");
            }

            // starting main menu loop
            mainMenu(connection);
        }
        catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Adds data to person table
     * @return person_ID of the person added to the database
     * @throws SQLException if there are duplicate values inputted for telNum, email.
     */
    public static int createPerson(String occupation, Connection connection) throws SQLException {
        String message1 = "Input the "+ occupation + " details\n";
        System.out.println(message1);
        // getting inputs
        int newid = -1 ;
        String newName = instance.getString("Name: ");
        String newSurname = instance.getString("Surname: ");
        String newEmail = instance.getString("Email Address: ");
        String newNumber = instance.getString("Telephone number: ");
        String newAddress = instance.getString("Physical Address: ");
        String addPerson = "INSERT INTO Person(name, surname, telNum, email, phyAdress, occupation) VALUES(?, ?, ?, ?, ?, ?)";
        try(PreparedStatement preparedStatement = connection.prepareStatement(addPerson, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newSurname);
            preparedStatement.setString(3, newNumber);
            preparedStatement.setString(4, newEmail);
            preparedStatement.setString(5, newAddress);
            preparedStatement.setString(6, occupation);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // return id of new person created
                        newid = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Failed to retrieve ID after creating person.");
                    }
                }
            }
        }
        catch (Exception b){
            System.out.println("Please ensure not to enter duplicate values");
            throw new SQLException("Error: Duplicate Values");
        }
        String message2 = occupation + " added to database[id = " + newid + "]";
        System.out.println(message2);
        return newid;
    }

    /**
     * Main menu, the user makes choices here
     */
    public static void mainMenu(Connection connection){
        System.out.println("Welcome to the Project creation program.");
        // main menu while loop
        while (true){
            String mainLoop = instance.getString("\n\t-\t-\t-\t-  MAIN  -\t-\t-\t-\t\n\nWhat would you like to do. " +
                    "Input only the number\n1) Create a new project\n2) View Projects\n3) Update Existing Projects\n4)" +
                    " Finalize a Project\n5) Quit\n");
            // switch and case for user to choose option
            // exact functions called
            switch (mainLoop){
                case "1":
                    int architect_id = -1;
                    int customer_id =-1;
                    int contractor_id=-1;
                    boolean continue_create = true;
                    // while loops used to allow user to input again if they make an error
                    while (continue_create){
                        try {
                            architect_id = createPerson("Architect", connection);
                            break;
                        }
                        catch (SQLException e){
                            boolean choice = toContinueLoop();
                            if (choice == true) {
                                // continue loop
                                continue;
                            }
                            if (choice == false) {
                                // break from loop
                                continue_create = false;
                                break;
                            }
                        }
                    }

                    while (continue_create){
                        try {
                            customer_id = createPerson("Customer", connection);
                            break;
                        }
                        catch (SQLException e){
                            boolean choice = toContinueLoop();
                            if (choice == true) {
                                // continue loop
                                continue;
                            }
                            if (choice == false) {
                                continue_create = false;
                                // break from loop
                                break;
                            }
                        }
                    }

                    while (continue_create){
                        try {
                            contractor_id = createPerson("Contractor", connection);
                            break;
                        }
                        catch (SQLException e){
                            boolean choice = toContinueLoop();
                            if (choice == true) {
                                // continue loop
                                continue;
                            }
                            if (choice == false) {
                                continue_create = false;
                                // break from loop
                                break;
                            }
                        }
                    }

                    while (continue_create){
                        try{
                            createProject(connection, customer_id, architect_id, contractor_id);
                            break;
                        }
                        catch (Exception b){
                            // choice if user wants to input again using toContinueLoop()
                            boolean choice = toContinueLoop();
                            if (choice == true) {
                                // continue loop
                                continue;
                            }
                            if (choice == false) {
                                continue_create = false;
                                // break from loop
                                break;
                            }
                        }
                    }
                    break;
                case "2":
                    // calls viewProject()
                    viewProject(connection);
                    break;
                case "3":
                    while (true){
                        try {
                            if (isProjectTableNotEmpty(connection)){
                                updateProject(connection);
                                break;
                            }
                            else {
                                System.out.println("You have no projects to edit");
                                break;
                            }
                        }
                        catch (Exception f){
                            boolean choice = toContinueLoop();
                            if (choice == true) {
                                // continue loop
                                continue;
                            }
                            if (choice == false) {
                                // break from loop
                                break;
                            }
                        }
                    }
                    break;
                case "4":
                    // check if there are an tables to finalize
                    if (isProjectTableNotEmpty(connection)){
                        finaliseProject(connection);
                    }
                    else {
                        System.out.println("You have no projects to finalise");
                        break;
                    }
                    break;
                case "5":
                    // quit
                    System.exit(0);
                default:
                    System.out.println("Incorrect Input");
                    break;
            }
        }
    }

    /**
     * Adds data to project table via user input
     * @throws ArithmeticException if error encountered or duplicate values.
     */
    public static void createProject(Connection connection, int customer_id, int architect_id, int contractor_id) throws SQLException {
        // user input to create project object
        String newProjName = instance.getString("Enter the project name: ");
        String newProjBuilding = instance.getString("Enter the type of building being made in the project: ");
        String newProjAddress = instance.getString("Enter the address of the project: ");
        String newErfNumber = instance.getString("Enter the erf number of the project: ");
        int newTotalExpense = instance.getInt("Enter the total expense of the project: ");
        int newExpensePaid = instance.getInt("Enter the expense paid by so far by the client: ");

        System.out.println("Enter the deadline values of year, month and day: ");
        int newYearDate = instance.getInt("Year: ");
        int newMonthDate = instance.getInt("Month: ");
        int newDayDate = instance.getInt("Day: ");

        String newDeadline = (newYearDate) + "-" + (newMonthDate) + "-" + (newDayDate);

        // SQL queries
        String insertQuery = "INSERT INTO Project(proj_name, proj_building, proj_address, proj_erf_number, total_expense," +
                " expense_paid, deadline, complete, complete_date, customer_id, architect_id, contractor_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        String selectQuery = "SELECT name, surname FROM Person WHERE person_ID = ?";

        // Prepared Statement, setting values to statement after
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
             PreparedStatement retrieveStatement = connection.prepareStatement(selectQuery)){
            String name;
            String surname;
            // if the input for name of the project is empty, new proj_name is created
            if (newProjName == "") {
                retrieveStatement.setInt(1, customer_id);
                try (ResultSet resultSet = retrieveStatement.executeQuery()) {
                    if (resultSet.next()) {
                        name = resultSet.getString("name");
                        surname = resultSet.getString("surname");
                    }
                    else {
                        throw new SQLException("Failed to retrieve ID after creating person.");
                    }
                    newProjName = name + " " + surname + " " + newProjBuilding;
                }
            }

            // setting values to prepared statement
            preparedStatement.setString(1, newProjName);
            preparedStatement.setString(2, newProjBuilding);
            preparedStatement.setString(3, newProjAddress);
            preparedStatement.setString(4, newErfNumber);
            preparedStatement.setInt(5, newTotalExpense);
            preparedStatement.setInt(6, newExpensePaid);
            preparedStatement.setString(7, newDeadline);
            preparedStatement.setBoolean(8, false);
            preparedStatement.setNull(9, Types.DATE);
            preparedStatement.setInt(10, customer_id);
            preparedStatement.setInt(11, architect_id);
            preparedStatement.setInt(12, contractor_id);
            preparedStatement.executeUpdate();
        }
        catch (SQLException b){
            System.out.println("Incorrect data type input");
            throw new SQLException("Incorrect data type input");
        }
    }

    //
    /**
     * function to give user a choice to quit to main menu or continue loop
     */
    public static boolean toContinueLoop() {
        boolean toContinue;
        String choiceToContinue = instance.getString("Incorrect input! Input 1 if you'd like to try again, 2 if you want to quit:\n");
        // choice given to re-try inputting values or quit to main menu
        if (Objects.equals(choiceToContinue, new String("1"))) {
            toContinue = true;
        }
        else if (Objects.equals(choiceToContinue, new String("2"))) {
            toContinue = false;
        }
        // if incorrect input again, break from loop
        else {
            System.out.println("Incorrect input, again");
            toContinue = false;
        }
        return toContinue;
    }

    /**
     * Prints out specified projects to console
     */
    public static void viewProject(Connection connection){
        // project table is not empty
        if (isProjectTableNotEmpty(connection)){
            // SQL queries
            String searchProjectQuery = "SELECT * FROM Project WHERE proj_num = ? AND proj_name = ?";
            String finishedProjectQuery = "SELECT * FROM Project WHERE complete = true";
            String unfinishedProjectQuery = "SELECT * FROM Project WHERE complete = false";
            String overdueProjectQuery = "SELECT * FROM Project WHERE complete = false AND deadline < CURDATE()";
            String viewChoice = instance.getString("View Project: \n1) Search project\n2) Finished" +
                    " Projects\n3) Unfinished Projects\n4) Overdue Projects\n");
            switch (viewChoice){
                case "1":
                    int exists = executeAndPrintQuery(connection, searchProjectQuery, instance.getInt("Enter project number\n"),
                            instance.getString("Enter project name\n"));
                    if (exists==-1){
                        System.out.println("Cannot find project");
                    }
                    break;
                case "2":
                    executeAndPrintQuery(connection, finishedProjectQuery);
                    break;
                case "3":
                    executeAndPrintQuery(connection, unfinishedProjectQuery);
                    break;
                case "4":
                    executeAndPrintQuery(connection, overdueProjectQuery);
                    break;
            }
        }
    }

    /**
     * Updates records within project table
     *
     * @throws Exception if any error is encountered
     */
    public static void updateProject(Connection connection) throws Exception{
        String searchProjectQuery = "SELECT * FROM Project WHERE proj_num = ? AND proj_name = ?";
        // search for project, return project_id
        int proj_id = executeAndPrintQuery(connection, searchProjectQuery,
                instance.getInt("Enter project number\n"), instance.getString("Enter project name\n"));
        String selectProject = "SELECT * FROM Project WHERE proj_num = ?";

        // getting person id's to use in update
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectProject)){
            preparedStatement.setInt(1, proj_id);
            int customer_id = -1;
            int architect_id = -1;
            int contractor_id = -1;
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    customer_id = resultSet.getInt("customer_id");
                    architect_id = resultSet.getInt("architect_id");
                    contractor_id = resultSet.getInt("contractor_id");
                }
                else {
                    System.out.println("Invalid project id");
                    return;
                }
            }

            // user input
            String updateChoice = instance.getString("What would you like to update? \n1) " +
                    "Project details\n2) Total expense paid\n3) Customer details\n4)" + " Architect" +
                    " details\n5) Contractor details\n");

            switch (updateChoice){
                case "1":
                    // user input
                    String projNameUpdate = instance.getString("Enter project name: ");
                    String projBuildingUpdate = instance.getString("Enter building type: ");
                    String projAddressUpdate = instance.getString("Enter project address: ");
                    String projErfUpdate = instance.getString("Enter erf number: ");
                    System.out.println("Enter the deadline values of year, month and day: ");
                    int newYearDate = instance.getInt("Year: ");
                    int newMonthDate = instance.getInt("Month: ");
                    int newDayDate = instance.getInt("Day: ");
                    String newDeadline = (newYearDate) + "-" + (newMonthDate) + "-" + (newDayDate);
                    int projExpenseUpdate = instance.getInt("Enter total expense: ");

                    // SQL query
                    String updateProjectQuery = "UPDATE Project SET proj_name = ?, proj_building = ?, proj_address = ?," +
                            " proj_erf_number = ?, total_expense = ?, deadline = ? WHERE proj_num = ?";
                    try (PreparedStatement preparedStatement1 = connection.prepareStatement(updateProjectQuery)){
                        preparedStatement1.setString(1, projNameUpdate);
                        preparedStatement1.setString(2, projBuildingUpdate);
                        preparedStatement1.setString(3, projAddressUpdate);
                        preparedStatement1.setString(4, projErfUpdate);
                        preparedStatement1.setInt(5, projExpenseUpdate);
                        preparedStatement1.setString(6, newDeadline);
                        preparedStatement1.setInt(7, proj_id);
                        preparedStatement1.executeUpdate();
                    }
                    System.out.println("Project Updated.\n");
                    break;

                case "2":
                    // getting user input
                    int projPaidUpdate = instance.getInt("Enter paid expense: ");
                    // SQL query
                    String updateExpensePaidQuery = "UPDATE Project SET expense_paid = ? WHERE proj_num = ?";

                    try (PreparedStatement preparedStatement1 = connection.prepareStatement(updateExpensePaidQuery)){
                        preparedStatement1.setInt(1, projPaidUpdate);
                        preparedStatement1.setInt(2, proj_id);
                        preparedStatement1.executeUpdate();
                    }
                    System.out.println("Project Updated.\n");
                    break;
                case "3":
                    // calling updatePerson
                    String occupation_cus = "Customer";
                    updatePerson(connection, occupation_cus, customer_id);
                    System.out.println("Project Updated.\n");
                    break;
                case "4":
                    String occupation_arch = "Architect";
                    updatePerson(connection, occupation_arch, architect_id);
                    System.out.println("Project Updated.\n");
                    break;
                case "5":
                    String occupation_cont = "Contractor";
                    updatePerson(connection, occupation_cont, contractor_id);
                    System.out.println("Project Updated.\n");
                    break;
                default:
                    System.out.println("Incorrect input");
                    break;
            }
        }
        catch (Exception b){
            System.out.println("Error: Duplicate values/Incorrect input");
            throw new Exception("Incorrect input");
        }

    }

    /**
     * Updates data is person table
     *
     * @param connection database connection
     * @param occupation Person's occupation
     * @param id Person's id
     */
    public static void updatePerson(Connection connection, String occupation, int id){
        String message1 = "Input the " + occupation + " details";
        System.out.println(message1);

        // getting user input
        String newName = instance.getString("Name: ");
        String newSurname = instance.getString("Surname: ");
        String newEmail = instance.getString("Email Address: ");
        String newNumber = instance.getString("Telephone number: ");
        String newAddress = instance.getString("Physical Address: ");
        // SQL query
        String updateQuery = "UPDATE Person SET name = ?, surname = ?," +
                " telNum = ?, email = ?, phyAdress = ?, occupation = ? WHERE person_ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)){
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newSurname);
            preparedStatement.setString(3, newNumber);
            preparedStatement.setString(4, newEmail);
            preparedStatement.setString(5, newAddress);
            preparedStatement.setString(6, occupation);
            preparedStatement.setInt(7, id);
            preparedStatement.executeUpdate();
            String message2 = occupation + " has been updated\n";
            System.out.println(message2);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Finalises project, prints out invoice
     */
    public static void finaliseProject(Connection connection){
        String searchProjectQuery = "SELECT * FROM Project WHERE proj_num = ? AND proj_name = ?";
        // search for user project, and returns the project id
        int proj_id = executeAndPrintQuery(connection, searchProjectQuery,
                instance.getInt("Enter project number\n"), instance.getString("Enter project name\n"));
        String selectProject = "SELECT * FROM Project WHERE proj_num = ?";
        // if project exits
        if (proj_id != -1){
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectProject)){
                preparedStatement.setInt(1, proj_id);
                boolean complete;
                String proj_name;
                int total_expense;
                int expense_paid;
                int customer_id;
                // getting customer data from table
                try (ResultSet resultSet = preparedStatement.executeQuery()){
                    if (resultSet.next()){
                        proj_name = resultSet.getString("proj_name");
                        total_expense = resultSet.getInt("total_expense");
                        expense_paid = resultSet.getInt("expense_paid");
                        complete = resultSet.getBoolean("complete");
                        customer_id = resultSet.getInt("customer_id");
                    }
                    else {
                        throw new SQLException("Invalid project id");
                    }
                }
                if (complete == false){
                    // updating project
                    String updateComplete = "UPDATE Project SET complete = ?, complete_date = ? WHERE proj_num = ?";
                    try (PreparedStatement preparedStatement1 = connection.prepareStatement(updateComplete)){
                        preparedStatement1.setBoolean(1, true);
                        preparedStatement1.setDate(2, Date.valueOf(LocalDate.now()));
                        preparedStatement1.setInt(3, proj_id);
                        preparedStatement1.executeUpdate();
                        printInvoice(connection, proj_name, total_expense, expense_paid, customer_id);
                        System.out.println("Project Complete");
                    }
                }
                else{
                    System.out.println("This project is already complete");
                }
            }
            catch (SQLException f){
                f.printStackTrace();
            }
        }
        else{
            System.out.println("This project does not exist");
        }
    }
    /**
     * checks if project table is empty
     * @return true if not empty, false if empty
     */
    public static boolean isProjectTableNotEmpty(Connection connection) {
        String countQuery = "SELECT COUNT(*) AS projectCount FROM Project";
        try (PreparedStatement countStatement = connection.prepareStatement(countQuery)) {
            try (ResultSet countResult = countStatement.executeQuery()) {
                if (countResult.next()) {
                    int projectCount = countResult.getInt("projectCount");
                    return projectCount > 0;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false in case of an exception or if the count query fails
    }

    /**
     * Prints out project data
     */
    public static void printProjectRecords(ResultSet resultSet) throws SQLException {
        // Customize the formatting based on your needs
        System.out.println("\n------------------------------\n");
        System.out.println("Project ID: " + resultSet.getInt("proj_num"));
        System.out.println("Project Name: " + resultSet.getString("proj_name"));
        System.out.println("Building Type: " + resultSet.getString("proj_building"));
        System.out.println("Adrress: " + resultSet.getString("proj_address"));
        System.out.println("Erf number: " + resultSet.getString("proj_erf_number"));
        System.out.println("Total expense: " + resultSet.getString("total_expense"));
        System.out.println("Expense Paid: " + resultSet.getString("expense_paid"));
        System.out.println("Deadline: " + resultSet.getString("deadline"));
        System.out.println("Complete: " + resultSet.getString("complete"));
        Date completionDate = resultSet.getDate("complete_date");
        if (resultSet.wasNull()) {
            System.out.println("Completion Date: NULL");
        } else {
            System.out.println("Completion Date: " + completionDate);
        }
        System.out.println("Customer ID: " + resultSet.getString("customer_id"));
        System.out.println("Architect ID: " + resultSet.getString("architect_id"));
        System.out.println("Contractor ID: " + resultSet.getString("contractor_id"));
        System.out.println("------------------------------\n");
    }

    /**
     * Executes a query with the given parameters on the provided database connection.
     * Prints the results using the printProjectRecords method and returns the project ID.
     *
     * @param connection  The database connection to execute the query on.
     * @param query       The SQL query to execute.
     * @param parameters  The parameters to be set in the prepared statement.
     * @return The project ID retrieved from the query result.
     */
    public static int executeAndPrintQuery(Connection connection, String query, Object...parameters){
        int id = -1 ;
        // Execute the query and print the project records
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){
            int index = 1;
            for (Object parameter: parameters){
                preparedStatement.setObject(index++, parameter);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()){
                    //id  = resultSet.getInt("proj_num");
                    printProjectRecords(resultSet);
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        // Retrieve the project ID from the executed query
        try (PreparedStatement preparedStatement1 = connection.prepareStatement(query)){
            int index = 1;
            for (Object parameter: parameters){
                preparedStatement1.setObject(index++, parameter);
            }
            try (ResultSet resultSet = preparedStatement1.executeQuery()){
                while (resultSet.next()){
                    id  = resultSet.getInt("proj_num");
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        return id;
    }

    /**
     * Checks if table exists in database
     *
     * @param connection  The database connection to execute the query on.
     * @param table_name  The table name to check if it exists
     * @return true if table exists
     */
    private static boolean isTableExists(Connection connection, String table_name) throws SQLException{
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet results = meta.getTables(null,null, table_name, new String[]{"TABLE"})){
            return results.next();
        }
    }

    /**
     * Prints an invoice for a completed project, including details such as project name,
     * completion status, and outstanding fees if applicable.
     *
     * @param connection   The database connection to execute the query on.
     * @param proj_name    The name of the project for which the invoice is being generated.
     * @param total_expense The total expenses associated with the project.
     * @param expense_paid The amount already paid for the project.
     * @param customer_id  The ID of the customer associated with the project.
     */
    public static void printInvoice(Connection connection, String proj_name, int total_expense, int expense_paid, int customer_id){
        // Check if there are outstanding fees
        if (total_expense > expense_paid){
            int outstanding = total_expense - expense_paid;
            String customerQuery = "SELECT * FROM Person WHERE person_ID = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(customerQuery)){
                preparedStatement.setInt(1, customer_id);
                try (ResultSet resultSet = preparedStatement.executeQuery()){
                    if (resultSet.next()){
                        // Retrieve customer details for the given customer_id
                        String name = resultSet.getString("name");
                        String surname = resultSet.getString("surname");
                        String email = resultSet.getString("email");
                        String number = resultSet.getString("telNum");
                        String address = resultSet.getString("phyAdress");

                        // Print the invoice details
                        System.out.println("-------------------");
                        System.out.println("Project Name: " + proj_name);
                        System.out.println("Completion Status: COMPLETE" );
                        System.out.println("Contact Details of " + name + " " + surname + ":");
                        System.out.println("Phone Number: " + number);
                        System.out.println("Email: " + email);
                        System.out.println("Address: " + address);
                        System.out.println("OUTSTANDING FEES: " + outstanding);
                    }
                }
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
}
