#check for unique reaction combinations
setwd("D:/BellosData/GF_3/Models")
library(stringr)
library(gtools)

getGens <- function(v) {
  pat <- paste0("(", paste0(v, collapse="|"), ")")
  return(pat)
}

countSimilarReactions <- function(file1, file2){ #file1 = model 1, file2 = model2
  
  fd1 = read.table(file1, sep = ",", header = T, stringsAsFactors = F)
  fd2 = read.table(file2, sep = ",", header = T, stringsAsFactors = F)
  
  fd1$Activity = as.numeric(as.character(fd1$Activity))
  fd2$Activity = as.numeric(as.character(fd2$Activity))
  
  filteredFD1 = fd1 %>% filter(Activity > 0.0000)
  filteredFD2 = fd2 %>% filter(Activity > 0.0000)
  
  
  return(length(intersect(filteredFD1$Reaction,filteredFD2$Reaction)))
  rm(fd1,fd2, filteredFD1, filteredFD2)
}

getModelNum <- function(x){
  return(as.numeric(unlist(str_extract_all(x,"\\d+"))[2]))
}


clusteringWithCommonReactions <- function(generationNo){
  
  pat <- paste0("^Generation_Flux_", getGens(generationNo), "_Model_\\d+\\.txt$")
  reactionFiles = mixedsort(list.files(pattern = pat))
  
  rowCombination = split(v<-t(utils::combn(450, 2)), seq(nrow(v))); rm(v)
  
  similarity = lapply(rowCombination, 
                      function(x) countSimilarReactions(reactionFiles[x[1]],reactionFiles[x[2]]))
  
  similarityDF <- data.frame(M1 = sapply(rowCombination, `[`, 1),
                             M2 = sapply(rowCombination, `[`, 2),
                             similarity_metric = do.call(rbind, similarity))
  
  
  write.table(similarityDF, 
              paste0("D:/BellosData/GF_3/Clustering_CommonReactions/Gen_",generationNo,"_similarityMtrx.txt"), 
              sep = ",", quote = F, row.names = F)
  
  #create the dissimilarity matrix
  maxNum = max(similarityDF$similarity_metric)
  normalizeData = similarityDF[,c(1:3)]
  normalizeData[c('Common')] <- lapply(normalizeData[c('similarity_metric')], function(x) 1 - x/maxNum)
  normalizeData[1:2] <- lapply(normalizeData[1:2], factor, levels=unique(unlist(normalizeData[1:2]))) 
  distMatrixN = xtabs(similarity_metric~M1+M2, data=normalizeData)
  distMatrixN = distMatrixN + t(distMatrixN)
  
  
  #hierarchical clustering 
  
  hcN <- hclust(as.dist(distMatrixN))
  hcdN <-as.dendrogram(hcN)
  png(paste0("D:/BellosData/GF_3/Clustering_CommonReactions/Gen_",generationNo,"_hclust.png"), height = 700, width = 1200)
  plot(hcdN, xlab = "Height", horiz = F , 
       main = paste0("Common Reactions (filtered > 0.0000) - Generation ",generationNo))
  dev.off()
  
  rm(distMatrixN,similarityDF)
  
}


sTime = Sys.time()

gens <- c(0,10,20, 30,  40,50,60, 70, 80,  90, 100, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200,
          210, 220, 230, 240, 249)

lapply(gens, function(x) clusteringWithCommonReactions(x))

eTime = Sys.time()
tTime = eTime - sTime

