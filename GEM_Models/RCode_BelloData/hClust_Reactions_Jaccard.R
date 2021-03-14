#check for unique reaction combinations
setwd("D:/BellosData/GF_3/Models")
library(stringr)
library(gtools)
library(dplyr)

getGens <- function(v) {
  pat <- paste0("(", paste0(v, collapse="|"), ")")
  return(pat)
}

computerJaccardDissimilarityR <- function(file1, file2){ #file1 = model 1, file2 = model2
  
  fd1 = read.table(file1, sep = ",", header = T, stringsAsFactors = F)
  fd2 = read.table(file2, sep = ",", header = T, stringsAsFactors = F)
  
  fd1 = fd1[fd1$Reaction !='Biomass', ]
  fd2 = fd2[fd2$Reaction !='Biomass', ]
  
  fd1$Activity = as.numeric(as.character(fd1$Activity))
  fd2$Activity = as.numeric(as.character(fd2$Activity))
  
  filteredFD1 = filter(fd1, Activity > 0.0000)
  filteredFD2 = filter(fd2, Activity > 0.0000)
  
  #Jaccard dissimilarity
  jaccardSimilarity = length(intersect(filteredFD1$Reaction,filteredFD2$Reaction))/length(union(filteredFD1$Reaction, filteredFD2$Reaction))
  return(1 - jaccardSimilarity)
  
  rm(fd1,fd2, filteredFD1, filteredFD2)
}

getModelNum <- function(x){
  return(as.numeric(unlist(str_extract_all(x,"\\d+"))[2]))
}


clusteringWithCommonReactions <- function(generationNo){
  
  pat <- paste0("^Generation_Flux_", getGens(generationNo), "_Model_\\d+\\.txt$")
  reactionFiles = mixedsort(list.files(pattern = pat))
  
  rowCombination = split(v<-t(utils::combn(450, 2)), seq(nrow(v))); rm(v)
  
  jdisimilarity = lapply(rowCombination, 
                      function(x) computerJaccardDissimilarityR(reactionFiles[x[1]],reactionFiles[x[2]]))
  
  similarityDF <- data.frame(M1 = sapply(rowCombination, `[`, 1),
                             M2 = sapply(rowCombination, `[`, 2),
                             Jaccard_dissimilarity = do.call(rbind, jdisimilarity))
  
  
  write.table(similarityDF, 
              paste0("D:/BellosData/GF_3/Clustering_Reactions_Jaccard/Gen_",generationNo,"_jaccardDisimilarityMtrx.txt"), 
              sep = ",", quote = F, row.names = F)
  
  #create the dissimilarity matrix
  #Jaccard dissimilarity
  
  distMatrixN = xtabs(Jaccard_dissimilarity~M1+M2, data=similarityDF)
  distMatrixN = distMatrixN + t(distMatrixN)
  
  
  #hierarchical clustering 
  
  hcN <- hclust(as.dist(distMatrixN))
  hcdN <-as.dendrogram(hcN)
  png(paste0("D:/BellosData/GF_3/Clustering_Reactions_Jaccard/Gen_",generationNo,"_jaccard_hclust.png"), height = 700, width = 1200)
  plot(hcdN, xlab = "Height", horiz = F , 
       main = paste0("Common Reactions (filtered > 0.0000) with Jaccard dist - Generation ",generationNo))
  dev.off()
  
  rm(distMatrixN,similarityDF)
  
}


sTime = Sys.time()

gens <- c(140, 249)

lapply(gens, function(x) clusteringWithCommonReactions(x))

eTime = Sys.time()
tTime = eTime - sTime

