# Plan de Test Bêta – Soodora

## 1. Sélection Des Fonctionnalités Principales

La version bêta de Soodora doit démontrer sa valeur essentielle tout en garantissant une expérience stable et cohérente pour les premiers testeurs. Après analyse, nous avons identifié trois fonctionnalités clés à inclure :

| **Feature**                                                           | **Description**                                                                                                                                                                                                                                                                                                       | **Priorité (Haute/Moyenne/Basse)** |
|------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------|
| Algorithme de recommandation pour la découverte d’artistes et de contenus  | Conçu pour mettre en avant les contenus les plus attrayants pour un utilisateur et aider les utilisateurs à découvrir des artistes émergents                                                                                                                                                                          | Moyenne                             |
| Abonnement aux artistes et personnalisation du contenu                     | Dans la page Explore en deux espaces : une section d’exploration pour découvrir de nouveaux artistes et tendances, et une page personnalisée dédiée aux abonnements. Les utilisateurs peuvent s’abonner à leurs artistes préférés et accéder facilement à leurs publications mises en avant dans cet espace exclusif. | Haute                               |
| Création et exportation de contenus pour les artistes                      | Les artistes pourront publier des posts attractifs sous différents formats : annonces, sorties musicales, événements, drops de merch, etc. Chaque post bénéficiera d’un rendu visuel optimisé pour capter l’attention et qui pourra être exporté sous un format adapté aux réseaux sociaux.                           | Haute                               |

### Changement depuis la Tech3
Soodora a changé de direction entre la Tech3 et la Tech4 suite à des retours concrets des différents besoins du marché et une remise en question au sein de l'équipe. Ce repositionnement s’est accompagné d’un rebranding complet, avec une nouvelle identité visuelle. Soodora est une plateforme pensée pour permettre aux artistes de se faire connaître et évoluer, tout en offrant aux passionnés de musique un moyen simple de découvrir de nouveaux talents. Avec une page d’exploration interactive, un outil puissant d’exportation de posts et une marketplace intégrée, Soodora crée un lien direct entre les artistes et leur public.

---

## 2. Scénarios de Test Bêta
### 2.1 Rôles des utilsiateurs

| **Rôles**            | **Description**                                                                                                                                                                                                                                                                                                                                  |
|----------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Utilisateur Standard | Ce rôle est destiné aux passionnés de musique souhaitant découvrir de nouveaux talents. Les utilisateurs standard bénéficient d’un accès complet à Soodora pour découvrir de nouveaux artistes via la page "exploration", s’abonner aux artistes, interagir avec les contenus postés et profiter d’un algorithme de recommandation personnalisé. |
| Artiste Standard     | Ce rôle s’adresse aux artistes qui souhaitent se faire connaître et engager leur public. Les artistes standard peuvent créer et publier divers formats de contenus (annonces, sorties musicales, événements, etc.), bénéficier d'un accès au marketplace et développer leur communauté via la plateforme.                                                                                                                                                                                                                                                                                                                             |
| Artiste Prenium      | Les artistes Prenium profitent d’une visibilité accrue, d’outils d’exportation performants et d’opportunités de rentabilité accrue via des commissions moindres sur leur merchandising, par exemple. Ce statut permet de valoriser et diffuser leur univers musical de manière optimale.                                                                                                                                                                                                                                                                                                                              |

### 2.2 Scénarios de Test
Nous avons élaboré plusieurs scénarios de test afin de valider ces fonctionnalités et d’assurer une expérience fluide pour les utilisateurs.

#### Scénario 1 : Recommandations personnalisées
- **Rôle utilisateur :** Utilisateur Standard  
- **Fonctionnalité testée :** Algorithme de recommandation  
- **Objectif :** Vérifier que l’utilisateur reçoit des recommandations pertinentes en fonction de son activité et de ses abonnements.  
- **Preconditions :** L’utilisateur a un compte actif et est connecté. 
- **Étapes du test :** 
  1. Créer un compte et parcourir la section Explore  
  2. Interagir avec plusieurs posts et produits du marketplace
  3. Vérifier si les recommandations évoluent en fonction des interactions  
  4. S’assurer que les recommandations sont pertinentes et diversifiées  
- **Résultat attendue :** L’utilisateur reçoit des recommandations adaptées à ses préférences et interactions. La diversité du contenu recommandé est respectée. Les recommandations évoluent dynamiquement en fonction des nouvelles interactions.

#### Scénario 2 : Abonnement et personnalisation du contenu
- **Rôle utilisateur :** Utilisateur Standard 
- **Fonctionnalité testée :** Système d’abonnement aux artistes  
- **Objectif :** Vérifier que les abonnements sont bien pris en compte et que le contenu des artistes suivis apparaît en priorité.  
- **Preconditions :** L’utilisateur a un compte actif et est connecté. Des artistes sont disponibles dans la section Explore.
- **Étapes du test :** 
  1. S’abonner à plusieurs artistes depuis la section Explore  
  2. Accéder à la section Explore et Marketplace pour voir si le contenu des artistes suivis est mis en avant  
  3. Vérifier la gestion des abonnements (désabonnement, mise à jour des flux)  
- **Résultat attendue :** Le contenu des artistes suivis apparaît en priorité dans l’Explore et le Marketplace. L’utilisateur peut facilement se désabonner et voir les mises à jour en temps réel. L’algorithme ajuste la mise en avant du contenu en fonction des abonnements de l’utilisateur.

#### Scénario 3 : Création de publications
- **Rôle utilisateur :** Artiste 
- **Fonctionnalité testée :** Création de posts  
- **Objectif :** Vérifier que les artistes peuvent créer des publications.  
- **Preconditions :** L’artiste est connecté à son compte. L’outil de création de publication est accessible.
- **Étapes du test :** 
  1. Accéder à l’outil de création de publication  
  2. Ajouter du contenu (texte, image, lien musical, produit)  
  3. Publier et vérifier l’affichage dans l’application
- **Résultat attendue :** L’artiste peut ajouter différents types de contenu sans erreurs. Le post s’affiche correctement dans l’application, avec les éléments bien formatés. Les utilisateurs peuvent voir et interagir avec la publication.

#### Scénario 4 : Exportation de publications
- **Rôle utilisateur :** Artiste Prenium
- **Fonctionnalité testée :** Exportation de posts  
- **Objectif :** Vérifier que les artistes peuvent exporter efficacement sur leurs réseaux sociaux.  
- **Preconditions :** L’artiste Prenium est connecté à son compte. L’outil de création de publication est accessible. L’artiste a déjà publié au moins un post.
- **Étapes du test :** 
  1. Accéder à l’outil de création de publication
  2. Ajouter du contenu (texte, image, lien musical, produit)
  3. Publier et vérifier l’affichage dans l’application
  4. Exporter le post sur un réseau social et vérifier la mise en page sur celle-ci
- **Résultat attendue :** L’exportation du post se fait sans erreurs technique. La mise en page est conservée sur le réseau social.

---

## 3. Couverture des Parcours Utilisateur Clés

Les scénarios de test ont été conçus pour refléter les usages réels des utilisateurs. Les parcours clés validés dans cette phase sont :

- **Découverte de nouveaux artistes et produits via l’algorithme de recommandation**  
- **Suivi des artistes et personnalisation du contenu affiché**  
- **Interaction des artistes avec leur audience à travers des publications et des exports vers d’autres plateformes**  

---

## 4. Critères de Réussite

Pour valider la réussite de la bêta, nous utilisons les critères suivants :

- **Stabilité** : L’application doit fonctionner sans erreurs critiques ni crashs susceptibles de perturber l’expérience des utilisateurs.
- **Performance** : Le temps de chargement des pages et des recommandations doit être optimisé afin d’offrir une navigation fluide et réactive.
- **Pertinence des recommandations** : L’algorithme de suggestion doit proposer du contenu aligné avec les préférences des utilisateurs. L’efficacité du système sera mesurée par le taux d’engagement sur ces recommandations notamment à travers les interactions générées autour de celles-ci.
- **Efficacité du système d’abonnement** : Les utilisateurs abonnés à des artistes doivent voir leurs publications mises en avant de manière claire et accessible dans leur espace personnalisé. Ce critère garantit que l’abonnement offre une réelle valeur ajoutée en facilitant l’accès aux contenus préférés des utilisateurs.
- **Qualité de l’export des publications** : Lorsqu’un post est exporté vers d’autres plateformes, il doit conserver son apparence initiale et être adapté aux spécificités de chaque canal de diffusion. L’optimisation de l’export garantit une présentation soignée et une visibilité accrue du contenu des artistes.

---

## 5. Bug Connues et Limitations

| **Bug** | **Description** | **Impact** | **Correction Prévu? (Oui/Non)** |
|---------|-----------------|------------|---------------------------------|
| x       | x               | x          | Non                             |

---

## 6. Conclusion

Ce plan de test n'est pas seulement une feuille de route technique : il incarne l'engagement de Soodora à révolutionner la manière dont les artistes et leurs publics interagissent. En évaluant rigoureusement la version bêta, nous visons à confirmer la stabilité, la performance et la pertinence de chaque fonctionnalité.

Ce plan représente un levier essentiel pour recueillir des retours constructifs qui orienteront les prochaines phases de développement. Les tests couvrent des scénarios allant de la personnalisation des recommandations à l'interaction des artistes via la création et l'exportation de contenus. Ceci offrent une vision d'ensemble de l'utilisation réelle de Soodora. Les enseignements tirés permettront non seulement de corriger les éventuelles "bug" mais aussi de renforcer la proposition de valeur de Soodora.

Pour conclure, cette démarche vise à transformer Soodora en une plateforme incontournable dans l'univers musical, garantissant une transition harmonieuse vers une version stable et complète. Ce plan de test est le point de lancement vers la concrétisation de la vision de Soodora: établir un lien direct, innovant et durable entre les artistes et leur public.
**_Soodora, là où la musique trouve son public._**