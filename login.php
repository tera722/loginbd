<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

// Conexión a la base de datos
$host = "localhost";
$dbname = "mi_base_datos";
$username = "root";
$password = "";

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    echo json_encode(["success" => false, "message" => "Error al conectar a la base de datos"]);
    exit();
}

// Obtener los datos enviados
$data = json_decode(file_get_contents("php://input"), true);
$usuario = $data["usuario"];
$contraseña = $data["contraseña"];

// Verificar si el usuario existe
$stmt = $pdo->prepare("SELECT * FROM usuarios WHERE usuario = :usuario");
$stmt->bindParam(":usuario", $usuario);
$stmt->execute();

if ($stmt->rowCount() == 1) {
    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    // Verificar la contraseña
    if (password_verify($contraseña, $user["contraseña"])) {
        echo json_encode(["success" => true, "message" => "Login exitoso", "id" => $user["id"]]);
    } else {
        echo json_encode(["success" => false, "message" => "Contraseña incorrecta"]);
    }
} else {
    echo json_encode(["success" => false, "message" => "Usuario no encontrado"]);
}
?>
