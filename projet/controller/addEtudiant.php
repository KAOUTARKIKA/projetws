<?php
include_once '../racine.php';
include_once RACINE.'/service/EtudiantService.php';
extract($_GET);

$dateNaissance = isset($_GET['dateNaissance']) ? $_GET['dateNaissance'] : null;
$photo = isset($_GET['photo']) ? $_GET['photo'] : null;

$es = new EtudiantService();
$es->create(new Etudiant(null, $nom, $prenom, $ville, $sexe, $dateNaissance, $photo));
header("location:../index.php");