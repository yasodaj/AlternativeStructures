#get the common core reactions for each cluster

setwd("D:/BellosData/GF_3/Models")

clusterFiles = gtools::mixedsort(list.files(pattern = "^Generation_Flux_\\d+_Model_\\d+.txt$"))

getReactions <- function(model){
  
  modelData = read.table(model, sep = ",", header = T, stringsAsFactors = F)
  
  return(modelData$Reaction)
  
}


allTheReactions = Reduce(union, lapply(clusterFiles, function(x) getReactions(x)))

allTheReactionsFiltered = allTheReactions[allTheReactions != c("Biomass")]

allTheReactionsFiltered = allTheReactionsFiltered[!is.na(allTheReactionsFiltered)]

allTheReactionsFiltered <- append(length(allTheReactionsFiltered),allTheReactionsFiltered)

write.table(allTheReactionsFiltered, "AllTheReaction.txt", sep = "\\n", row.names = F, quote = F, col.names = F)
