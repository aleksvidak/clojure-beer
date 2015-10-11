# clojure-beer

This is a web application designed to provide users manipulation with beer data acquired from www.beeradvocate.com website. It is written in Clojure using enlive, ring, compojure, incanter and friend libraries.

This web application allows users with user privileges to score beers and get user & beer recommendations based on their beer scoring.
It also allows users with admin privileges to create new users (admins or users), edit them & delete them. Admins can create new beers & delete existing ones.

The dataset is acquired from http://jmcauley.ucsd.edu/cse255/data/beer/ url and it is about 1.5GB large after unpacking it. Because of the great amount of data stored in the file it is hard to manipulate the file. It was necessary to split file into smaller chunks of data so the process of changing the data structure could be possible. I used split bash command to divide the file into pieces of 100MB. 
Then I needed to get these chunks into a .csv format that I could import into mongodb. I needed to go from a structure that had records in blocks of 14 lines, with a property field on every line to a .csv file that had a row per record, and fields in comma-separated columns. I used google-refine tool to do that. I used the incanter library to read the .csv file and congomongo to insert it into the mongodb beer_data collection.
From there on there are two more collections created in the database, users and beers collections. Users for storing user credentials and beers for making distinct beer collection (new & old beers).

## Usage
Because this application is using mongo database you need to install mongodb (I used version 3.0.6) following installation instructions found on https://docs.mongodb.org/manual/. To use this application you need to install leiningen also (PC users can follow instructions on http://leiningen-win-installer.djpowell.net/).

To initialize the database with the necesseary data you need to set :main to input.csv-dataset in project.clj file. Then you run cmd and navigate to the project folder and type run lein. After database is filled with the data message "Initialization done!" will be displayed on the console.
To start the web application you will need to change :main to start in project.clj file. You can now find web app in you browser by typing http://localhost:8080/.

##Remarks
From the begining of my work on this application I tried to widen my knowledge of functional programming. Two books helped me a lot to understand data structures and functional programming in general:Living Clojure by Carin Meier & Clojure Programming by Chas Emerick, Brian Carper, and Christophe Grand. Dealing with some tasks about movies recommendations in the school classes and Collective Inteligence book helped me write functions related to recommendations. I can say I had a lot of issues making friend library work because of using congomongo library and inability to write namespaced keywords into the database as friend library expects. After some thought, I decided to make simple user collection in the database & then transform it dynamically so I can be able to authorize users as friend allows. In the begining I tried to use cosine similarity, but after implementing it I realized it doesn't work well with this kind of recommendations (it is used for document recommendations) and I switched to eucli. dist.
After finishing the project I can say I learned a lot and I look forward to incorporating my Java Web Service for extracting keywords & keyphrases into some future Clojure project.

##References
- Dataset transformation http://blog.bruggen.com/2015/04/importing-snap-beeradvocate-dataset.html
- Mongodb https://docs.mongodb.org/manual/
- Login form http://bootsnipp.com/snippets/featured/custom-login-registration-amp-forgot-password
- Importing dataset into mongodb http://data-sorcery.org/2010/01/03/datasets_mongodb/
- Recommendations http://nakkaya.com/2009/11/11/euclidean-distance-score/
- https://github.com/cgrand/enlive
- https://github.com/ring-clojure/ring
- https://github.com/weavejester/compojure
- https://github.com/aboekhoff/congomongo

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
