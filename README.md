# Cascadia

Une implémentation du jeu de société Cascadia (Java 23).

## Description

Cascadia est un jeu de stratégie où 1 à 4 joueurs cherchent à créer l'environnement le plus accueillant pour la faune locale dans la région de Cascadia. Cette version numérique propose à la fois une interface en ligne de commande et une interface graphique utilisant la bibliothèque Zen.

## Prérequis

- Java 17 ou supérieur
- Apache Ant pour la compilation
- La bibliothèque Zen (incluse dans le dossier `lib`)

## Installation

1. Clonez le dépôt
2. Assurez-vous que le fichier `zen-6.0.jar` est présent dans le dossier `lib`
3. Dans Eclipse :
   - Ajoutez le dossier `lib` dans le répertoire du projet
   - Faites un clic droit sur `zen-6.0.jar`
   - Sélectionnez Build Path > Add to Build Path

## Compilation et Exécution

### Avec Ant

```bash
# Compiler les sources
ant compile

# Créer le jar exécutable (commande par défaut)
ant jar

# Générer la Javadoc
ant javadoc

# Nettoyer le projet
ant clean
```

### Exécution

```bash
java -jar Cascadia.jar
```

### Depuis Eclipse

1. Importez le projet en tant que "Projet Java existant"
2. Exécutez la classe `Main.java` dans le package `graphical.main`

## Architecture

Le projet suit une architecture MVC (Modèle-Vue-Contrôleur) et est organisé en plusieurs packages principaux :

- `core` : Logique métier du jeu
- `graphical` : Interface graphique (utilisant Zen)
- `terminal` : Interface en ligne de commande
- `images` : Ressources graphiques

## Fonctionnalités

- Support de 2 à 4 joueurs
- Deux interfaces au choix :
  - Interface graphique avec Zen
  - Interface en ligne de commande
- Plusieurs variantes de jeu :
  - Mode famille
  - Mode intermédiaire
  - Mode standard avec cartes de score personnalisées
- Support des tuiles carrées et hexagonales
- Système de scoring adaptatif selon la variante choisie

## Documentation

- La documentation utilisateur se trouve dans `docs/user.pdf`
- La documentation technique est disponible dans `docs/dev.pdf`
- La Javadoc peut être générée avec `ant javadoc`

## Implémentation avec Zen

L'interface graphique utilise la bibliothèque Zen fournie (`zen-6.0.jar`). Le code suit le pattern MVC avec :

- `SimpleGameData` : Gestion des données du jeu (Modèle)
- `GameView` : Interface pour l'affichage graphique (Vue)
- Contrôleur : Implémente la boucle de jeu et gère les événements utilisateur

## Auteurs
 
- [Mathieu J.](https://github.com/Zenomium)  
- [Chami R.](https://github.com/RaedChami)  


## Licence

Ce projet a été développé dans un cadre académique à l'Institut Gaspard-Monge.
