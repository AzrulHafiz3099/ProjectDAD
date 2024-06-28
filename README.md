**Application Involve**

**1) Insert Application**

Insert App
- Let the user to Insert new equipment into the database

**Layer Diagram**

![diagram drawio](https://github.com/AzrulHafiz3099/ProjectDAD/assets/165458463/4f1cf943-1c09-47a1-b831-481a531a04dd)

1.User Interface Layer:
User Interface: 

- Components like JFrame, JTextField, and JButton are used to create the GUI.

2.Middleware Layer:

REST API

- Endpoint: `POST /saveDataInventory.php` Receives inventory data (name and type) from the client, processes it, and saves it to the database.

- Data Processing: Processes the incoming data using PHP. Receives data from the client application (UI). Processes and validates the received data.

- Database Interaction: Interacts with the MySQL database and executes the SQL INSERT query to save the data, and sends a response back to the client.


3.Database Layer:

- Database projectdad

- Table: equipment

- Columns: name (Name of the equipment item) and type (Type of the equipment item).

**Functions/Features in the Middleware**

- Add equipment item using endpoint `POST /saveDataInventory.php`

- Function to accept name and type parameters, validates them and insert it into the 'equipment' table in database



**2) Rent Application**

Rent App
- Let the user to Search and Update the equipment

**Layer Diagram**

![Image](https://github.com/users/AzrulHafiz3099/projects/1/assets/111211731/3430db4b-bcbb-4f25-aafa-e237eff7c7c6)

1. Presentation Layer
Components: JFrame, JTextArea, JTextField, JButton, JLabel
Responsibilities:
- Provides the graphical user interface (GUI) for user interaction.
- Displays data fetched from the server.
- Takes user input for item ID and status.
- Invokes actions like fetching data, searching for an item, and saving updates.

2. Application Logic Layer
Components: Methods within the Rent class (fetchDataFromTable, searchItemById, saveStatus, displayData)
Responsibilities:
- Implements the core logic for handling user actions.
- Manages the flow of data between the presentation layer and middleware.
- Processes data fetched from the server and updates the GUI accordingly.
- Validates user inputs and handles error messages.

3. Middleware / Communication Layer
Components: Socket, DataOutputStream, DataInputStream
Responsibilities:
- Manages communication between the client application and the server.
- Sends requests to the server and receives responses.
- Handles network-related exceptions and retries.

4. Database Layer
Components: External server application
Responsibilities:
- Provides data storage and retrieval services.
- Processes client requests (e.g., fetch data, update status).
- Sends data back to the client in a structured format (JSON).

**Apps Explanation :**

1) Fetching Data from Server:

When the "Get Item Data From Table" button is clicked, the app sends a request to the server to retrieve item data.
The server response, presumably in JSON format, is parsed and displayed in the JTextArea.

2) Searching for an Item by ID:

The user can input an item ID and click the "Search Item" button to find and display the item name and status.
The application searches through the displayed data and populates the relevant fields if a match is found.

3) Updating Item Status:

After searching for an item, the user can update its status.
The updated status is sent to the server when the "Save to Database" button is clicked.
The server's response indicates whether the update was successful, and the data display is refreshed accordingly.

**URL Endpoint Middleware** : TCP SOCKET

**Functions/Features in the Middleware**

Socket Initialization and Connection:

- Establishes a socket connection to the server using the specified IP address and port.
- Manages input and output streams for communication.

Data Fetching:

- Sends a "GET_DATA" command to the server.
- Reads and processes the server response containing item data.
- Converts the JSON response into a readable format for the client application.

Search Functionality:

- Processes the text input for item ID.
- Searches through the fetched data to find the item with the specified ID.
- Updates the GUI with the found item's details.

Data Update:

- Sends a "SAVE_STATUS" command along with the item ID and new status to the server.
- Reads and processes the server response indicating whether the update was successful.
- Updates the client GUI and refetches the data if necessary.

Error Handling:

- Catches and handles IO exceptions during server communication.
- Displays appropriate error messages to the user in case of communication failures.


**Database Involve**

Database Name = projectdad
Table Name = equipment

Data dictionary
----------------------------
| ID      | int (100)      | 
| name    | Varchar (100)  |
| type    | Varchar (100)  |
| status  | Varchar (100)  |
----------------------------
