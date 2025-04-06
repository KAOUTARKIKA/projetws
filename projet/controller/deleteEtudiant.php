<?php
include_once '../racine.php';
include_once RACINE.'/service/EtudiantService.php';
extract($_GET);
$es = new EtudiantService();
$etudiant = $es->findById($id);
if ($etudiant) {
    $es->delete($etudiant);
}
header("location:../index.php");