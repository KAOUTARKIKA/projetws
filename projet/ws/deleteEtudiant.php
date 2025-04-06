<?php
if($_SERVER["REQUEST_METHOD"] == "POST"){
    include_once '../racine.php';
    include_once RACINE.'/service/EtudiantService.php';
    delete();
}

function delete(){
    $id = $_POST['id'];
    $es = new EtudiantService();
    $etudiant = $es->findById($id);
    if ($etudiant) {
        $es->delete($etudiant);
    }

    // Retourner une réponse (soit un message de succès soit la liste mise à jour)
    header('Content-type: application/json');
    echo json_encode($es->findAllApi());
    exit();
}