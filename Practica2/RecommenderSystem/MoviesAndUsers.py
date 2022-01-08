import pandas as pd 
import math
from collections import OrderedDict
import warnings
warnings.filterwarnings('ignore')

class MoviesAndUsers:
    """Clase para almacenar los usuarios y las películas"""

    def __init__(self, item_file, data_file):
        self.__item_file = item_file
        self.__data_file = data_file
        self.__last_id = 944

    
    def read_files(self):
        """Método para leer los ficheros"""

        # Las columnas de los dataframes son
        # movies_data -> item_id, movie_title
        # users_data  -> user_id, item_id, rating
        # avg_rating_by_user -> user_id, rating

        # Leemos el fichero de las pelis
        self.__movies_data= pd.read_csv(self.__item_file, sep='|', 
        names=
        [
            'item_id', 'movie_title', 'release_date', 'video_release_date',
            'IMDb URL', 'Action', 'Adventure', 'Animation',
            'Children', 'Comedy', 'Crime', 'Documentary', 'Drama', 'Fantasy',
            'FilmNoir', 'Horror', 'Musical', 'Mystery', 'Romance',  'SciFi',
            'Thriller', 'War',  'Western'
        ])

        # Borramos las columnas que no nos hacen falta
        self.__movies_data.drop(columns=
        [
            'release_date', 'video_release_date',
            'IMDb URL', 'Action', 'Adventure', 'Animation',
            'Children', 'Comedy', 'Crime', 'Documentary', 'Drama', 'Fantasy',
            'FilmNoir', 'Horror', 'Musical', 'Mystery', 'Romance',  'SciFi',
            'Thriller', 'War',  'Western'
        ], axis=1, inplace=True)
        
        # Cargamos el fichero de los usuarios
        self.__users_data = pd.read_csv(self.__data_file, sep='\t', names=['user_id','item_id','rating','titmestamp'])
        
        # Borramos las columnas que no nos hagan falta
        self.__users_data.drop(columns='titmestamp', axis=1, inplace=True)

        # Creamos un nuevo dataframe que contiene el id de usuario y la media de sus valoraciones
        self.__avg_rating_by_user = pd.DataFrame(self.__users_data.groupby('user_id')[['rating']].mean())

    def rank_movies(self):
        """Devuelve 20 películas al azar"""

        movies = self.__movies_data.sample(n=20)
        item_ids = movies["item_id"].tolist()
        items_names = movies["movie_title"].tolist()

        return item_ids, items_names

    def add_new_user_data(self, ratings):
        """ Método para añadir datos de las valoraciones de un usuario 
            Recibe como parámetros:
                ratings -> diccionario item_id : rating 
            Devuelve el id del nuevo usuario """
        
        for item_id in ratings:
            new_row = pd.DataFrame([[self.__last_id, item_id, ratings[item_id]]], columns=['user_id','item_id','rating'])
            self.__users_data = pd.concat([self.__users_data, new_row], ignore_index=True)

        # Recalculamos las medias
        self.__avg_rating_by_user = pd.DataFrame(self.__users_data.groupby('user_id')[['rating']].mean())

        id_new_user =  self.__last_id
        self.__last_id = self.__last_id +1 

        return id_new_user


    def coef_pearson(self, active_user, other_user):
        """ Calcula el coeficiente de Pearson entre dos usuarios
            Recibe como parámetro el user_id de dos usuarios """

        # Calculamos la media del usuario y las películas que ha visto
        active_user_avg = self.__avg_rating_by_user.rating[active_user]
        active_user_movies = self.__users_data[self.__users_data.user_id == active_user]
        # Le restamos a la valoración de cada película la valoración media del usuario
        active_user_movies['rating'] = -active_user_avg + active_user_movies['rating']
        
        # Calculamos la media del usuario y las películas que ha visto
        other_user_avg = self.__avg_rating_by_user.rating[other_user]
        other_user_movies = self.__users_data[self.__users_data.user_id == other_user]
        # Le restamos a la valoración de cada película la valoración media del usuario
        other_user_movies['rating'] = -other_user_avg + other_user_movies['rating']

        # Se sacan las pelis en común de los dos usuarios y se calcula la sumatoria
        common_movies = pd.merge(active_user_movies, other_user_movies, on=['item_id'])
        common_movies['sum'] = common_movies['rating_x']*common_movies['rating_y']
        common_movies['rating_x_2'] = common_movies['rating_x'] * common_movies['rating_x']
        common_movies['rating_y_2'] = common_movies['rating_y'] * common_movies['rating_y']
        
        numerator = common_movies['sum'].sum()

        denominator_1 = common_movies['rating_x_2'].sum()
        denominator_2 = common_movies['rating_y_2'].sum()

        result =numerator / ( math.sqrt(denominator_1) * math.sqrt(denominator_2) )

        return result


    def nearest_neighbors(self, k, active_user):
        """ Calcula los k vecinos más cercanos de un usuario
            k = tamaño del vecindario
            active_user = id del usuario al que se le van a buscar los vecinos"""

        number_of_users = len(self.__avg_rating_by_user.index)
        sim = {}

        for i in range(1, number_of_users+1):
            if i != active_user:
                sim[i] = self.coef_pearson(active_user, i)

        df = pd.DataFrame(list(sim.items()), columns=['user_id', 'pearson'])
        df = df.sort_values('pearson', ascending=False)
        
        result = df.head(k)['user_id'].tolist() 

        return result


    def recommendation(self, k, active_user):
        """ Hace una recomendación al usuario que se pasa como argumento 
            k = tamaño del vecindario
            active_user = id del usuario al que se le van a buscar los vecinos"""

        # Calcular los vecinos
        neighbors = self.nearest_neighbors(k, active_user)

        # Calcular pelis del usuario activo
        active_user_movies = self.__users_data[self.__users_data.user_id == active_user]
        active_user_movies = active_user_movies["item_id"].tolist()

        # Calcular las pelis que no ha visto el usuario
        neighbors_movies = self.__users_data[self.__users_data.user_id.isin(neighbors)]
        neighbors_movies = neighbors_movies[~neighbors_movies.item_id.isin(active_user_movies)]

        # Para cada valoración
        #   Restamos la valoración media del usuario
        neighbors_movies = pd.merge(neighbors_movies, self.__avg_rating_by_user, on=['user_id'])
        neighbors_movies['rating_avg'] = neighbors_movies['rating_x'] - neighbors_movies['rating_y']
        
        #   Multiplicamos por la similitud de ese usuario con el usuario activo
        neighbors_movies['sim'] = neighbors_movies['user_id'].apply(lambda x : self.coef_pearson(active_user, x))
        neighbors_movies['rating_avg_sim'] = neighbors_movies['rating_avg'] * neighbors_movies['sim']
        
        # Para cada peli
        # Usuarios que la han visto
        #   Calcular C 
        neighbors_movies_c = neighbors_movies.drop_duplicates(subset='user_id', keep="last")
        neighbors_movies_c = neighbors_movies_c['user_id'].tolist()
        c = 0
        for n in neighbors_movies_c:
            c += self.coef_pearson(active_user, n)

        c = 1/c

        #   Calcular valoración media del usuario activo
        active_user_avg = self.__avg_rating_by_user.rating[active_user]

        #   Calcular sumatoria
        neighbors_movies_group = pd.DataFrame(neighbors_movies.groupby('item_id')['rating_avg_sim'].sum())
        
        # Compensar diferencias de interpretación y escala
        neighbors_movies_group['rating_avg_sim'] = active_user_avg + c * neighbors_movies_group['rating_avg_sim']
        
        # Ordenarlas de valoración más alta a menos
        neighbors_movies_group = neighbors_movies_group.sort_values(by=['rating_avg_sim'], ascending=False)

        # Cruzarlas con los títulos
        result = pd.merge (neighbors_movies_group.head(5), self.__movies_data, on=['item_id'])

        # Devolver resultado en una lista
        return result["movie_title"].tolist()

        

# if __name__ == "__main__":
#     mv = MoviesAndUsers('../ml-data/u.item', '../ml-data/u.data')
#     mv.read_files()

#     print(mv.recommendation(10, 943))
        
