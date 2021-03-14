setwd("D:/BellosData/GF_3/Clustering_Reactions_Jaccard")

similarityDF = read.table("Gen_140_jaccardDisimilarityMtrx.txt", sep = ",", header = T, stringsAsFactors = F)
generationNo = 140

similarityDF = na.omit(similarityDF)

maxNum = max(similarityDF$Jaccard_dissimilarity)
normalizeData = similarityDF[,c(1:3)]
normalizeData[c('Common')] <- lapply(normalizeData[c('Jaccard_dissimilarity')], function(x) 1 - x) #x/maxNum)
normalizeData[1:2] <- lapply(normalizeData[1:2], factor, levels=unique(unlist(normalizeData[1:2]))) 
distMatrixN = xtabs(Jaccard_dissimilarity~M1+M2, data=normalizeData)
distMatrixN = distMatrixN + t(distMatrixN)


#hierarchical clustering 

hcN <- hclust(as.dist(distMatrixN), method = "complete")
hcdN <-as.dendrogram(hcN)
#png(paste0("Gen_",generationNo,"_hclust.png"), height = 700, width = 1200)
plot(hcN, xlab = "Height", horiz = F, 
     main = paste0("Common reactions (filtered > 0.0000) - Generation ",generationNo))
#dev.off()

#################################################################################################

install.packages("pvclust")

#################################################################################################

library(factoextra)
library(fpc)
library(NbClust)

hc.res <- eclust(as.dist(distMatrixN), "hclust", k = 5, 
                 hc_method = "complete", graph = FALSE)

fviz_dend(hc.res, show_labels = FALSE,
          palette = "jco", as.ggplot = TRUE, main = "")


fviz_silhouette(hc.res)


# Visualize dendrograms
png("Generation_140_hclust_CommonReactionJaccard_k_5.png",width = 1000, height = 700)
fviz_dend(hc.res, show_labels = FALSE,
          palette = "jco", as.ggplot = TRUE, main = "Biomass - Generation 138")

dev.off()

png("Biomass_Sil_Generation_140_CommonReactionJaccard_k_5.png",width = 1000, height = 700)
fviz_silhouette(hc.res)
dev.off()

#######################


modelDF = data.frame(ModelNo = c(1:450))
modelDF = cbind(modelDF, group = (hc.res$cluster))
silGroups = 5

for(i in 1:silGroups){
  modelDFTemp<- subset(modelDF, group==i)
  
  models <- modelDFTemp$ModelNo
  models <- append(paste0("Total_",length(models)), models)
  
  write.table(models,paste0("Gen_",generationNo,"_COmmonReactionsJaccard_hClustMemebers_K_",silGroups,"_memeber_",i,".txt"),
              quote = F, row.names = F, col.names = F)
}

rm(distMatrixN,similarityDF)
