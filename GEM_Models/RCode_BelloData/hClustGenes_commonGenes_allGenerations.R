#generate dendrograms for all the generations

library(ggplot2)
library(ggdendro)
library(gtools)

setwd("D:/BellosData/GC_3/Preprocessed")

getGens <- function(v) {
  pat <- paste0("(", paste0(v, collapse="|"), ")")
  return(pat)
}

computeSimilarity <- function(fileName){
  
  generationNo = str_extract(fileName, "\\d+")
  
  rawData = read.table(fileName, sep = ",", header = T, stringsAsFactors = F)
  
  rawData <- as.data.frame(apply(rawData, 2, as.character))
  
  rawData <- t(rawData)
  
  rawData <- rawData[-1,]
  
  rawData <- as.data.frame(apply(rawData, 2, as.numeric))
  
  rowCombination = split(v<-t(utils::combn(450, 2)), seq(nrow(v))); rm(v)
  
  similarity = lapply(rowCombination, function(x) sum(rawData[x[1],]&rawData[x[2],]))
  
  similarityDF <- data.frame(M1 = sapply(rowCombination, `[`, 1),
                             M2 = sapply(rowCombination, `[`, 2),
                             similarity_metric = do.call(rbind, similarity))
  
  rawDataT <- NULL
  
  write.table(similarityDF, 
              paste0("D:/BellosData/GC_3/ClusteringWithAnd/Gen_",generationNo,"_similarityMtrx.txt"), 
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
  png(paste0("D:/BellosData/GC_3/ClusteringWithAnd/Gen_",generationNo,"_hclust.png"), height = 700, width = 1200)
  plot(hcdN, xlab = "Height", horiz = F , 
       main = paste0("Clustering by Common Genes (only 1s) - Generation ",generationNo))
  dev.off()
  
  distMatrixN <- NULL
  similarityDF <- NULL
  
  
}

sTime = Sys.time()

#gens <- c(0, 49, 99, 149, 199, 249)      # or any values you wish to use
gens <- c(0,10,20, 30,  40,50,60, 70, 80,  90, 100, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200,
          210, 220, 230, 240, 249)
pat <- paste0("^Generation_Chromosones_", getGens(gens), "_DF.txt$")

geneFiles = mixedsort(list.files(pattern = pat))

lapply(geneFiles, function(x) computeSimilarity(x))

eTime = Sys.time()
tTime = eTime - sTime


