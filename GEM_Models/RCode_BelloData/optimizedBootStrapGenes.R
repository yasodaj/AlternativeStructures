library(ggplot2)
library(ggdendro)
library(gtools)

setwd("D:/BellosData/GC_3/Preprocessed")

geneFiles = mixedsort(list.files(pattern = "^.*.txt$"))

geneFilesT <- geneFiles[1:10]

computeSimilarity <- function(fileName){
  
  generationNo = str_extract(fileName, "\\d+")
  
  rawData = read.table(fileName, sep = ",", header = T, stringsAsFactors = F)
  
  rawData <- as.data.frame(apply(rawData, 2, as.character))
  
  rawData <- t(rawData)
  
  rawData <- rawData[-1,]
  
  rawData <- as.data.frame(apply(rawData, 2, as.numeric))
  
  sampleNo = 20
  ss = 100
  
  for(i in 1:sampleNo){
    
    rawDataT <- rawData[sample(nrow(rawData), ss), ]
    
    rowNums = as.numeric(row.names(rawDataT))
    
    rowCombination = split(v<-t(utils::combn(rowNums, 2)), seq(nrow(v))); rm(v)
    
    similarity = lapply(rowCombination, function(x) sum(rawData[x[1],] == rawData[x[2],]))
    
    similarityDF <- data.frame(M1 = sapply(rowCombination, `[`, 1),
                               M2 = sapply(rowCombination, `[`, 2),
                               similarity_metric = do.call(rbind, similarity))
    
    rawDataT <- NULL
    
    write.table(similarityDF, 
                paste0("D:/BellosData/GC_3/TimeTrials/GeneData/Gen_",generationNo,
                       "_sampleSize_",ss,"_sample_",i,"_similarityMtrx.txt"), 
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
    png(paste0("D:/BellosData/GC_3/TimeTrials/GeneData/Gen_",generationNo,"_sampleSize_",
               ss,"_sample_",i,"_hclust.png"), height = 700, width = 1200)
    plot(hcdN, xlab = "Height", horiz = F , 
         main = paste0("Generation ",generationNo," Sample No ",i, " (Sample size ", ss,")"))
    dev.off()

    distMatrixN <- NULL
    similarityDF <- NULL
  }
  
  
}

lapply(geneFilesT, function(x) computeSimilarity(x))



