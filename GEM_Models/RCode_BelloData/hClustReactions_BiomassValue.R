#cluster based on biomass value

#selected = c("Biomass", "PFL")
#d1[d1$Reaction %in% selected,]
#d1 = read.table("Generation_Flux_0_Model_448.txt", sep = ",", header = T, stringsAsFactors = F)
#d2 = read.table("Generation_Flux_0_Model_449.txt", sep = ",", header = T, stringsAsFactors = F)
#a = d1[d1$Reaction %in% "Biomass",]$Activity - d2[d2$Reaction %in% "Biomass",]$Activity

setwd("D:/BellosData/GF_3/Models")


computeBiomassSimilarity <- function(model1, model2){
  
  model1Data = read.table(model1, sep = ",", header = T, stringsAsFactors = F)
  model2Data = read.table(model2, sep = ",", header = T, stringsAsFactors = F)
  
  diffB = as.numeric(model1Data[model1Data$Reaction %in% "Biomass",]$Activity) - as.numeric(model2Data[model2Data$Reaction %in% "Biomass",]$Activity)
  
  ifelse(length(diffB) == 0, return("NA"), return(diffB))
 
  rm(model1Data,model2Data)
  
}

getGens <- function(v) {
  pat <- paste0("(", paste0(v, collapse="|"), ")")
  return(pat)
}


clusteringWithBiomass <- function(generationNo){
  
  pat <- paste0("^Generation_Flux_", getGens(generationNo), "_Model_\\d+\\.txt$")
  reactionFiles = mixedsort(list.files(pattern = pat)) #models in one generation
  
  rowCombination = split(v<-t(utils::combn(450, 2)), seq(nrow(v))); rm(v)
  
  similarity = lapply(rowCombination, 
                      function(x) computeBiomassSimilarity(reactionFiles[x[1]],reactionFiles[x[2]]))
  
  similarityDF <- data.frame(M1 = sapply(rowCombination, `[`, 1),
                             M2 = sapply(rowCombination, `[`, 2),
                             similarity_metric = do.call(rbind, similarity))
  

  write.table(similarityDF, 
              paste0("D:/BellosData/GF_3/Clustering_BiomassSimilarity/Gen_",generationNo,"_similarityMtrx.txt"), 
              sep = ",", quote = F, row.names = F)
  
  similarityDF = na.omit(similarityDF)
  
  #create the dissimilarity matrix
  maxNum = max(similarityDF$similarity_metric)
  normalizeData = similarityDF[,c(1:3)]
  normalizeData[c('Common')] <- lapply(normalizeData[c('similarity_metric')], function(x) x/maxNum)
  normalizeData[1:2] <- lapply(normalizeData[1:2], factor, levels=unique(unlist(normalizeData[1:2]))) 
  distMatrixN = xtabs(similarity_metric~M1+M2, data=normalizeData)
  distMatrixN = distMatrixN + t(distMatrixN)
  
  
  #hierarchical clustering 
  
  hcN <- hclust(as.dist(distMatrixN))
  hcdN <-as.dendrogram(hcN)
  png(paste0("D:/BellosData/GF_3/Clustering_BiomassSimilarity/Gen_",generationNo,"_hclust.png"), height = 700, width = 1200)
  plot(hcdN, xlab = "Height", horiz = F , 
       main = paste0("Biomass Value - Generation ",generationNo, "- (Models ",nrow(similarityDF),")"))
  dev.off()
  
  rm(distMatrixN,similarityDF)
 
  
}

sTime = Sys.time()

gens <- c(140)

lapply(gens, function(x) clusteringWithBiomass(x))

eTime = Sys.time()
tTime = eTime - sTime


