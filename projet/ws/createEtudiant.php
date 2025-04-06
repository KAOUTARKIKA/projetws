<?php
if($_SERVER["REQUEST_METHOD"] == "POST"){
    include_once("../racine.php");
    include_once RACINE.'/service/EtudiantService.php';

    try {
        // Récupération des données
        $nom = $_POST['nom'];
        $prenom = $_POST['prenom'];
        $ville = $_POST['ville'];
        $sexe = $_POST['sexe'];
        $dateNaissance = isset($_POST['dateNaissance']) ? $_POST['dateNaissance'] : null;

        // Gestion de l'image
        $photo = null;
        if(isset($_POST['photo']) && isset($_POST['photoName'])) {
            $imageData = base64_decode($_POST['photo']);
            $fileName = $_POST['photoName'];
            $filePath = RACINE.'/uploads/'.$fileName;

            // Sauvegarde de l'image
            if(file_put_contents($filePath, $imageData)) {
                $photo = $fileName;
            }
        }

        $es = new EtudiantService();
        $etudiant = new Etudiant(
            null,
            $nom,
            $prenom,
            $ville,
            $sexe,
            $dateNaissance,
            $photo
        );

        $es->create($etudiant);

        // Retourner une réponse JSON cohérente
        header('Content-type: application/json');
        echo json_encode([
            'success' => true,
            'message' => 'Étudiant ajouté avec succès',
            'data' => $es->findAllApi()
        ]);

    } catch(Exception $e) {
        header('Content-type: application/json');
        http_response_code(500);
        echo json_encode([
            'success' => false,
            'message' => 'Erreur serveur: '.$e->getMessage()
        ]);
    }
    exit();
}