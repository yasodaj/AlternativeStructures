setwd("D:/BellosData/GF_3/Models")


getGens <- function(v) {
  pat <- paste0("(", paste0(v, collapse="|"), ")")
  return(pat)
}

getBiomassValue<-function(model){
  
  modelData = read.table(model, sep = ",", header = T, stringsAsFactors = F)
  
  biomassVal = modelData[modelData$Reaction %in% "Biomass",]$Activity
  
  ifelse(length(biomassVal) == 0, return("NA"),return(biomassVal))
  
  
}


getBiomassValueForGeneration <- function(generationNo){
  
  pat <- paste0("^Generation_Flux_", getGens(generationNo), "_Model_\\d+\\.txt$")
  reactionFiles = mixedsort(list.files(pattern = pat)) #models in one generation
  
  rowCombination = split(v<-t(utils::combn(450, 1)), seq(nrow(v))); rm(v)
  
  biomass = lapply(rowCombination, 
                      function(x) getBiomassValue(reactionFiles[x[1]]))
  
  biomassDF <- data.frame(M = sapply(rowCombination, `[`, 1),
                             biomassValue = do.call(rbind, biomass))
  
  
  write.table(biomassDF,paste0("D:/BellosData/GF_3/Plot_Biomass/Gen_",generationNo,"_biomass.txt"), sep = ",", quote = F, row.names = F)
}


sTime = Sys.time()

gens <- c(0:249)

lapply(gens, function(x) getBiomassValueForGeneration(x))

eTime = Sys.time()
tTime = eTime - sTime


