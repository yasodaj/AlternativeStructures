my.file.rename <- function(fileName) {
  
  from = paste0("D:/BellosData/GF_3/Reaction_Activity/",fileName)
  to = paste0("D:/BellosData/GF_3/Reaction_Activity/WithBiomass/",fileName)
  todir <- dirname(to)
  
  if (!isTRUE(file.info(todir)$isdir)) dir.create(todir, recursive=TRUE)
  file.rename(from = from,  to = to)
}


setwd("D:/BellosData/GF_3/Reaction_Activity")
rawFile = list.files(pattern = "^Gen_\\d+_ReactionActivity_biomass.png$")

lapply(rawFile, function(x) my.file.rename(x))
