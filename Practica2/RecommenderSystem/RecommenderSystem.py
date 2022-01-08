from __future__ import print_function, unicode_literals
from pprint import pprint
from PyInquirer import style_from_dict, Token, prompt, Separator
from examples import custom_style_2

from MoviesAndUsers import MoviesAndUsers

# Tamaño del vecondario
NEIGHBORHOOD = 10
# Ruta al archivo con los datos de los usuarios
USERS_FILE = "../ml-data/u.data"
# Ruta al archivo con los datos de las pelis
ITEM_FILE = "../ml-data/u.item"

# Inicicalizamos el recomendador
recommender = MoviesAndUsers(ITEM_FILE, USERS_FILE)
recommender.read_files()

# Función para parsear las respuestas
def get_score(answer):
    if (answer == 'Five'):
        return 5
    elif (answer == 'Four'):
        return 4
    elif (answer == 'Three'):
        return 3
    elif (answer == 'Two'):
        return 2
    else:
        return 1
    

# Primera pregunta ¿Quieres crear un usuario?
questions_1 = [
    {
        'type': 'confirm',
        'message': 'Do you want to make a new user?',
        'name': 'response',
        'default': True,
    }
]

answers_1 = prompt(questions_1, style=custom_style_2)


# Crea un nuevo usuario si la primera pregunta es true
if answers_1['response'] == True:
    # El nuevo usuario tiene que valorar 20 películas
    item_ids, item_names = recommender.rank_movies()
    ratings = {}
    
    for i in range(len(item_ids)):
        questions = [    
            {
                'type': 'list',
                'name': 'score',
                'message': 'What score do you give to ' + item_names[i] +'?',
                'choices': [
                    'Five',
                    'Four',
                    'Three',
                    'Two',
                    'One'
                ]
            }
        ]
        answers = prompt(questions, style=custom_style_2)
        ratings[item_ids[i]] =  get_score(answers['score'])
    
    id = recommender.add_new_user_data(ratings)

    # Una vez valoradas las películas se le hace una recomendación
    print ("We recommend you...: \n")
    result = recommender.recommendation(NEIGHBORHOOD, int(id))
    for movie in result:
        print (movie + "\n")

# Si la primera pregunta es False no crea el usuario 
else:
    # Pide el ID de un usuario del sistema
    questions_2 = [
        {
            'type': 'input',
            'message': 'Give me a user ID',
            'name': 'user_id',
        }
    ]
    answers_2 = prompt(questions_2, style=custom_style_2)
    print ("We recommend you...: \n")
    result = recommender.recommendation(NEIGHBORHOOD, int(answers_2['user_id']))
    for movie in result:
        print (movie + "\n")

