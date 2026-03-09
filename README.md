# Proyecto base para el Backend. Curso Modelos de Programación

## Enlaces de interés
- [Jenkins](http://200.69.103.29:8085/jenkins/)
- [SonarQube](http://200.69.103.29:8084/sonar/)

## GUIA SIMPLE DE FLUJO DE RAMAS (GIT)

### REGLAS IMPORTANTES
- main: rama estable (siempre debe compilar)
- develop: rama de integracion
- feature/*: ramas donde se desarrolla cada funcionalidad
- NUNCA trabajar directamente en main
- EVITAR trabajar directamente en develop


### 1. ACTUALIZAR REPOSITORIO
```
git checkout develop
git pull origin develop
```

### 2. CREAR UNA RAMA PARA TU FEATURE

```
git checkout -b feature/nombreDeLaFeature
```
ejemplo
```
git checkout -b feature/NotificationBase
```


### 3. TRABAJAR NORMALMENTE

```
git add .
git commit -m "feat: descripcion corta del cambio"
```

subir tu rama
```
git push origin feature/nombreDeLaFeature
```


### 4. ANTES DE HACER PULL REQUEST (MUY IMPORTANTE)

```
# traer cambios recientes
git fetch origin

# integrar develop en tu rama
git merge origin/develop

# si hay conflictos, resolverlos
git add .
git commit

# subir cambios actualizados
git push

```

### 5. CREAR PULL REQUEST

- ir a GitHub
- crear Pull Request hacia develop (NO HACIA MAIN)
- esperar revision de otro integrante

- **NUNCA aprobar tu propio PR**


### 6. DESPUES DEL MERGE
```
# volver a develop actualizado
git checkout develop
git pull origin develop

# borrar tu rama local
git branch -d feature/nombreDeLaFeature

# borrar rama remota (SOLO si se sabe que el feature termino)
git push origin --delete feature/nombreDeLaFeature

```

### RESUMEN RAPIDO
```
# actualizar develop
git checkout develop
git pull origin develop

# crear feature
git checkout -b feature/miFeature

# trabajar
git add .
git commit -m "mensaje"

# subir
git push origin feature/miFeature

# actualizar con develop antes del PR
git fetch origin
git merge origin/develop
git push
```
