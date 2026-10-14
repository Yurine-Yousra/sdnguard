alors ici je vais mettre toute les notes que je viens d'apprendre 
-le fichier pom.xml dans le module impl : contient les bundle dont j'ai bespin avec leur version
- qu'on je compile mon module j'aurai un bundle = .jar qui contient mes classes plus un fichier manifest qui contient des informations sur le jar et qui contient 
les packages que ce module import donc les packages dont il a besoin et les paclages qu'il export
donc le fichier pom.xml contient tout le bundle et le manifest contient que le package donc on peut installer un bundle juste pour utiliser un ou deux packages 
on a un groupe ou bien on l'appelle un projet par ex MDSAL qui contient des bundles qui ont des verions et dans chaque bundle on trouve des pcakges et un package se forme d'un
ensemble de classe et d'interface java 
les dependances dans le fichiers pom.xml sont des dependances ODL donc on les trouve pas dans le depot maven donc on doit mettre  a jour le depot maven pour dire que pour installer les dependances de ce projet et qui sont listées dans ce fichier pom.xml il ne faut pas consulter le depot maven par defaut mais il faut consulter un autre emplacement qui est le karaf/system
le karaf est un conteneur ou on peut executer l'application ODL 
