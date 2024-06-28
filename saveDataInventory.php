<?php

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "projectdad";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

if (isset($_POST['name']) && isset($_POST['type'])) {
    $name = $_POST['name'];
    $type = $_POST['type'];
    $status = "AVAILABLE";

    // Prepare and bind
    $stmt = $conn->prepare("INSERT INTO equipment (name, type, status) VALUES (?, ?, ?)");
    $stmt->bind_param("sss", $name, $type, $status);

    if ($stmt->execute()) {
        echo "Data saved successfully!";
    } else {
        echo "Error: " . $stmt->error;
    }

    $stmt->close();
} else {
    echo "Invalid input.";
}

$conn->close();
?>
