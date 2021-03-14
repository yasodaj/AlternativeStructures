
library(cluster)
library(dplyr)

solutionSamples = 5

for(s in 1:solutionSamples){
  
  # pairWiseDistanceFileName = paste0("E:\\LayoutComparison\\Toy\\RMSDWithRotation\\",
  #                                   "Sample",s,"_newTest\\PairwiseRMSD_ToySample_",s,".txt")
  pairWiseDistanceFileName = "E:\\LayoutComparison\\Toy\\RMSDWithRotation\\NewSample_NewGridSize_WithAlternativeSolutions_5k_Run9\\Run9_uniqueElite_PairWiseDistance.txt"
  
  #meta data
  sections = unlist(strsplit(pairWiseDistanceFileName,"\\\\"))
  model = sections[3]
  sample = "Run9"#sections[5]
  filePath = paste(sections[1],sections[2],sections[3],sections[4],
                   sections[5],sep = "\\")
  
  data = read.table(pairWiseDistanceFileName, sep = "\t", stringsAsFactors = F)
  colnames(data) <- c("L1", "L2", "Dist","isRotated", "Rotation")
  data <- data[,c(1:3)]
  data$scaled <- (data$Dist-min(data$Dist))/(max(data$Dist)-min(data$Dist))
  
  dat2 <- data %>% mutate_each_(funs(scale(.) %>% as.vector), 
                               vars=c("Dist"))
  
  
  #create matrix
  
  Un1 <-  as.character(unique(unlist(data[1:2])))
  data[1:2] <- lapply(data[1:2], factor, levels = Un1)
  yourMatrix <- xtabs(Dist ~ L1 + L2, data = data)
  yourMatrix[lower.tri(yourMatrix)] <- t(yourMatrix)[lower.tri(yourMatrix)]
  
  
  # Un1 <-  as.character(unique(unlist(dat2[1:2])))
  # dat2[1:2] <- lapply(dat2[1:2], factor, levels = Un1)
  # yourMatrix <- xtabs(Dist ~ L1 + L2, data = dat2)
  # yourMatrix[lower.tri(yourMatrix)] <- t(yourMatrix)[lower.tri(yourMatrix)]
  
  
  #create heatmap
  heatmapFileName=paste0(filePath,"\\",model,"_",sample,"_","heatMapS.png")
  png(heatmapFileName, width = 1100, height = 700)
  heatmap(yourMatrix, main = paste0("Heat map ",sample))
  dev.off()
  
  
  #hierarchical clustering
  h <- hclust(dist(yourMatrix), method = "complete")
  
  #save the dendrogram
  dendFileName=paste0(filePath,"\\",model,"_",sample,"_","dend.png")
  png(dendFileName, width = 1100, height = 700)
  plot(h, hang = -1, main = paste0(model," ",sample," - ","Cluster Dendrogram"))
  dev.off()
  
  
  
  #selecting the best sil value
  
  silDF <- data.frame("noClust" = character(), "AvgWidth" = numeric())
  
  m = 2#min(h$height)
  n = 10#max(h$height)
  
  for(nClust in m:n){

    sil_cl <- silhouette(cutree(h, k=nClust) ,dist(yourMatrix), title=title(main = 'Good'))
    silSummaryObj = summary(sil_cl)
    avgSilWidth = silSummaryObj$avg.width
    silDF <- rbind(silDF, data.frame(noClust = nClust, AvgWidth = avgSilWidth))  
    
  }
  
  optimalClusters = silDF[silDF$AvgWidth == max(silDF$AvgWidth),][[1]]
  
  #cut the tree and save the dendrogram
  dendFileName=paste0(filePath,"\\",model,"_",sample,"_","rectDend.png")
  png(dendFileName, width = 1100, height = 700)
  plot(h, hang = -1, main = paste0(model," ",sample," - ","Cluster Dendrogram - K = ",
                                   optimalClusters))
  rect.hclust(h, k = optimalClusters, border = 2:7)
  dev.off()
  
  
  #sil plot
  silFileName=paste0(filePath,"\\",model,"_",sample,"_","silPlot.png")
  sil_cl <- silhouette(cutree(h, k=optimalClusters) ,dist(yourMatrix), title=title(main = 'Good'))
  png(silFileName, width = 700, height = 400)
  plot(sil_cl, main = paste0(model," ",sample," - ","Silhouette Plot"))
  rownames(sil_cl) <- rownames(yourMatrix)
  dev.off()
  
  
  #write cluster constituents in to files
  
  clusterConst <- cutree(h, k = optimalClusters)
  
  for(x in 1:optimalClusters){
    
    clusterConstData = rownames(yourMatrix)[clusterConst == x]
    clustConstFile = paste0(filePath,"\\","hCluster_K_",optimalClusters,"_const_",
                            x,".txt")
    write.table(clusterConstData,clustConstFile , sep = "\n", quote = F,
                col.names = F, row.names = F)
  }
  
  
}


