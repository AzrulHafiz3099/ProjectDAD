<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "projectdad";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

error_log("PHP script started");

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    if (isset($_POST['action'])) {
        $action = $_POST['action'];
        error_log("Action: " . $action);
        switch ($action) {
            case 'get_data':
                $sql = "SELECT * FROM equipment";
                $result = $conn->query($sql);
                $data = array();
                if ($result->num_rows > 0) {
                    while ($row = $result->fetch_assoc()) {
                        $data[] = $row;
                    }
                }
                echo json_encode($data);
                break;

            case 'search':
                if (!isset($_POST["id"])) {
                    die(json_encode(array('error' => 'Missing ID parameter')));
                }
                $id = $_POST["id"];
                $stmt = $conn->prepare("SELECT name, status FROM equipment WHERE id = ?");
                if ($stmt === false) {
                    die(json_encode(array('error' => "Prepare failed: " . $conn->error)));
                }

                $stmt->bind_param("s", $id);
                $stmt->execute();
                $stmt->bind_result($name, $status);
                if ($stmt->fetch()) {
                    echo json_encode(array('name' => $name, 'status' => $status));
                } else {
                    echo json_encode(array('error' => 'Item not found'));
                }
                $stmt->close();
                break;

                case 'save':
                    if (!isset($_POST["id"]) || !isset($_POST["status"])) {
                        die(json_encode(array('error' => 'Missing parameters')));
                    }
                    $id = $_POST["id"];
                    $status = $_POST["status"];
                    $stmt = $conn->prepare("UPDATE equipment SET status = ? WHERE id = ?");
                    if ($stmt === false) {
                        die(json_encode(array('error' => "Prepare failed: " . $conn->error)));
                    }
                
                    $stmt->bind_param("ss", $status, $id);
                    if ($stmt->execute()) {
                        echo json_encode(array('status' => 'success'));
                    } else {
                        echo json_encode(array('error' => 'Failed to update status'));
                    }
                    $stmt->close();
                    break;

            default:
                echo json_encode(array('error' => 'Invalid action'));
                break;
        }
    } else {
        echo json_encode(array('error' => 'No action specified'));
    }
} else {
    echo json_encode(array('error' => 'Invalid request method'));
}

$conn->close();
error_log("PHP script ended");
?>