#check for duplicate reaction cores
setwd("D:/BellosData/GF_3/Models")

getGens <- function(v) {
  pat <- paste0("(", paste0(v, collapse="|"), ")")
  return(pat)
}

compareReactionCores <- function(model1, model2){
  
  fd1 = read.table(model1, sep = ",", header = T, stringsAsFactors = F)
  fd2 = read.table(model2, sep = ",", header = T, stringsAsFactors = F)
  
  fd1 = fd1[fd1$Reaction !='Biomass', ]
  fd2 = fd2[fd2$Reaction !='Biomass', ]
  
  filteredFD1 = filter(fd1, Activity > 0.0000)
  filteredFD2 = filter(fd2, Activity > 0.0000)
  
  return(length(union(setdiff(filteredFD1$Reaction, filteredFD2$Reaction), setdiff(filteredFD2$Reaction, filteredFD1$Reaction))))
  
}


checkDuplicateReactionCores <- function(generationNo){
  
  pat <- paste0("^Generation_Flux_", getGens(generationNo), "_Model_\\d+\\.txt$")
  reactionFiles = gtools::mixedsort(list.files(pattern = pat))
  
  rowCombination = split(v<-t(utils::combn(450, 2)), seq(nrow(v))); rm(v)
  
  difference = lapply(rowCombination, 
                      function(x) compareReactionCores(reactionFiles[x[1]],reactionFiles[x[2]]))
  
  
  setDiffReactionsDF <- data.frame(M1 = sapply(rowCombination, `[`, 1),
                                   M2 = sapply(rowCombination, `[`, 2),
                                   difference = do.call(rbind, difference))
  
  write.table(setDiffReactionsDF, 
              paste0("D:/BellosData/GF_3/compare_ReactionCores/WithFiltering/Gen_",generationNo,"_reactionCoreComparison_withF.txt"), 
              sep = ",", quote = F, row.names = F)
  
}


sTime = Sys.time()

gens <- c(140, 249)

lapply(gens, function(x) checkDuplicateReactionCores(x))

eTime = Sys.time()
tTime = eTime - sTime

