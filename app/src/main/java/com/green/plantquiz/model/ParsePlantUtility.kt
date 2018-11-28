package com.green.plantquiz.model

import org.json.JSONArray
import org.json.JSONObject

class ParsePlantUtility {



    fun parsePlantObjectsFromJSONData() : List<Plant>? {




        var allPlantObjects: ArrayList<Plant> = ArrayList()
        var downloadingObject = DownloadingObject()
        var topLevelPLantJSONData = downloadingObject.
            downloadJSONDataFromLink("http://plantplaces.com/perl/mobile/flashcard.pl")
        var topLevelJSONObject: JSONObject = JSONObject(topLevelPLantJSONData)
        var plantObjectsArray: JSONArray = topLevelJSONObject.getJSONArray("values")

        var index: Int = 0

        while (index < plantObjectsArray.length()) { // * = 0 - * = 1 - * = 2 - * = 3 - * = 4


            var plantObject: Plant = Plant()
            var jsonObject = plantObjectsArray.getJSONObject(index)

            /*
            * genus: String, species: String, cultivar: String, common: String,
            pictureName: String, description: String, difficulty: Int, id: Int = 0)
            * */


            with(jsonObject) {
                plantObject.genus = getString("genus")
                plantObject.species = getString("species")
                plantObject.cultivar = getString("cultivar")
                plantObject.common = getString("common")
                plantObject.picture_name = getString("picture_name")
                plantObject.description = getString("description")
                plantObject.difficulty = getInt("difficulty")
                plantObject.id = getInt("id")
            }

            allPlantObjects.add(plantObject)

            index++

        }



        return allPlantObjects


    }


}