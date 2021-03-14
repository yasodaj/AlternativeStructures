setwd("D:/BellosData/GF_3/Models")

reNameModelNumber <- function(modelName){
  
  oldModelName = paste0("D:/BellosData/GF_3/Models/Temp/",modelName)
  
  nth = 2
  #building the new name with gsub 
  newModelName = gsubfn(paste0("^((?:\\D*\\d+){", nth-1, "}\\D*)(\\d+)"), 
                        function(x,y,z) paste0(x, as.numeric(y) + 1), modelName)
  
  newModelName = paste0("D:/BellosData/GF_3/Models/", newModelName)
    #rename
  file.rename(oldModelName, newModelName)
  
}


reactionModels = gtools::mixedsort(list.files(path = "D:/BellosData/GF_3/Models/Temp", 
                                              pattern = "^Generation_Flux_\\d+_Model_\\d+.txt$"))

lapply(reactionModels, function(x) reNameModelNumber(x))


