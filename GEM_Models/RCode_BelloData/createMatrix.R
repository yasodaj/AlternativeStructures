#convert gene summary into a matrix and hierarchical clustering of gene data

setwd("D:/BellosData/GC_3/Summary")

generationSummaryFiles = list.files(pattern = "^Generation.*_Summary.txt$")

for(i in 1:1){#length(generationSummaryFiles)){
  
  generationNo = as.numeric(str_extract(generationSummaryFiles[i],"\\d+"))
  
  summaryDF = read.table(generationSummaryFiles[i], sep = ",", header = T)
  
  #normalize data
  maxNum = max(summaryDF$Common)
  normalizeData = summaryDF[,c(1:3)]
  normalizeData[c('Common')] <- lapply(normalizeData[c('Common')], function(x) 1 - x/maxNum)
  
  normalizeData[1:2] <- lapply(normalizeData[1:2], factor, levels=unique(unlist(normalizeData[1:2]))) 
  
  distMatrixN = xtabs(Common~Model1+Model2, data=normalizeData)
  
  distMatrixN = distMatrixN + t(distMatrixN)
  
  hcN <- hclust(as.dist(distMatrixN))
  hcdN <-as.dendrogram(hcN)
  
  png(paste0("Gen_",generationNo,"_hclust_withNorm.png"), height = 600, width = 1000)
  
  plot(hcdN, xlab = "Height", horiz = T , main = paste0("Generation ",generationNo," With normalization"))
  dev.off()
  
  #without normalization
  summaryDF[1:2] <- lapply(summaryDF[1:2], factor, levels=unique(unlist(summaryDF[1:2]))) 
  
  distMatrix = xtabs(Common~Model1+Model2, data=summaryDF)
  
  distMatrix = distMatrix + t(distMatrix)
  
  hc <- hclust(as.dist(distMatrix))
  
  hcd <-as.dendrogram(hc)
  
  png(paste0("Gen_",generationNo,"_hclust_withOutNorm.png"), height = 600, width = 1000)
  
  plot(hcd, xlab = "Height", horiz = T , main = paste0("Generation ",generationNo," With out normalization"))
  dev.off()
  
  
  
  
}
