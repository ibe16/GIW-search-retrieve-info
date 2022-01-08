# Sistema de recomendación

Desarrollo de un Sistema de Recomendación basado en Filtrado Colaborativo para la asigantura de GIW.

La memoria completa sobre la práctica se puede encontrar [aquí][link_to_pdf].

[link_to_pdf]:https://docs.google.com/viewer?url=https://github.com/ibe16/GIW-search-retrieve-info/raw/main/Practica2/Pr%C3%A1ctica%204.pdf

El código de la práctica se ha desarrollado en Python usando las siguientes bibliotecas:
- **Pandas**: Una biblioteca para el análisis y tratamiento de datos.
- **PyInquirer**: Una biblioteca para realizar interfaces interactivas por terminal.

La práctica incluye dos archivos que tienen la siguiente funcionalidad:
- **MoviesAndUsers.py**: Incluye toda la logística para cargar los dataset y hacer los
cálculos sobre los datos.
- **RecommenderSystem.py**: Contiene la interfaz de la aplicación.

Para ejecutar la práctica:
1. Estando en el directorio /RecommenderSystem creamos un
entorno virtual con:
```
python3 -m venv env
```
2. Activamos el entorno virtual con:
```
source env/bin/activate
```
3. Instalamos las dependencias necesarias:
```
pip install -r requirements.txt
```
4. Ejecutamos la interfaz:
```
python3 RecommenderSystem.py
```
