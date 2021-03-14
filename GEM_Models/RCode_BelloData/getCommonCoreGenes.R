
setwd("D:/BellosData/GF_3/BiomassFiles")
library(stringr)
library(dplyr)




getModelNos <- function(v) {
  pat <- paste0("(", paste0(v, collapse="|"), ")")
  return(pat)
}


getCommonCoreGenes <- function(clusterFile, geneFile){
  
  generationNo = unlist(str_extract_all(geneFile, "\\d+"))[2]
  
  clusterNo = unlist(str_extract_all(clusterFile, "\\d+"))[3]
  
  modelData = read.table(clusterFile,sep = ",", header = T, stringsAsFactors = F)
  
  modelNums = unlist(lapply(as.numeric(modelData$x), function(x) x + 1 ))

  modelGenes = read.table(geneFile,header = T, sep = ",", stringsAsFactors = F)
  
  modelGenesCluster = modelGenes[,c("Gene",paste0("M_",modelNums))]
  
  commonCore = apply(modelGenesCluster[2:ncol(modelGenesCluster)], 1, function(x) all(x == 1))
  
  commonCoreDF = modelGenesCluster %>% filter(commonCore)
  
  commonCoreGenes = commonCoreDF$Gene
  
  write.table(commonCoreGenes, paste0("D:/BellosData/GF_3/BiomassFiles/CommonCoreGenes/CommonCoreGenes_Gen_",
                                      generationNo,"_clusterNo_",clusterNo,".txt"), sep = ",", quote = F, row.names = F)
}

# 
# gens = c(140)
# modelNo = c(0,1,2)
# 
# pat <- paste0("^Generation_Flux_", getGens(generationNo), "_Model_",getModelNos(modelNo),".txt$")
# reactionFiles = mixedsort(list.files(pattern = pat))

geneFile = "D:/BellosData/GC_3/Preprocessed/Generation_Chromosones_249_DF.txt"


#clusterFiles = list.files(pattern = "^Gen_\\d+_Biomass_hClustMemebers.*.txt$")
clusterFiles = "Gen_249_Biomass_hClustMemebers_K_1_memeber_1.txt"

lapply(clusterFiles, function(x) getCommonCoreGenes(x,geneFile))


###################################################################################################

#get the common core of common cores

setwd("D:/BellosData/GF_3/BiomassFiles/CommonCoreGenes")

commonCoreGeneFiles = gtools::mixedsort(list.files(path = "D:/BellosData/GF_3/BiomassFiles/CommonCoreGenes", 
                                                   pattern = "^CommonCoreGenes_Gen_\\d+_clusterNo.*.txt$"))


for(i in 1:5){
  
  tempDF = read.table(commonCoreGeneFiles[i], header = T, sep = ",", stringsAsFactors = F)
  
  noOfGenes = nrow(tempDF)
  print(noOfGenes)
  
  if(i == 1){
    
    commonOfCommon = tempDF$x
    
  }else{
    commonOfCommon = intersect(commonOfCommon, tempDF$x)
  }
  
}

write.table(commonOfCommon, "CommonOfCommon.txt", quote = F, row.names = F)
####################################################################################################



#get the difference in each comparison

setwd("D:/BellosData/GF_3/BiomassFiles/CommonCoreGenes")

geneSetDifferenceClusterMembers<- function(cluster1, cluster2){
  
  cluster1No = as.numeric(unlist(str_extract_all(cluster1, "\\d+"))[2])
  cluster2No = as.numeric(unlist(str_extract_all(cluster2, "\\d+"))[2])
  
  cluster1Data = read.table(cluster1, header = T, sep = ",", stringsAsFactors = F)
  cluster2Data = read.table(cluster2, header = T, sep = ",", stringsAsFactors = F)
  
  temp = data.frame(Clust1 = cluster1No, Clust2 = cluster2No, 
                    Set_diff_genes = setdiff(cluster1Data, cluster2Data), stringsAsFactors = F)

  write.table(temp,
              paste0("D:/BellosData/GF_3/BiomassFiles/CommonCoreGenes/Gen_140_clusters_",
                     cluster1No,"_",cluster2No,"_setDiff.txt"),
              sep = ",", quote = F, row.names = F)


}

rowCombination = split(v<-t(utils::combn(2, 2)), seq(nrow(v))); rm(v)

lapply(rowCombination, function(x) geneSetDifferenceClusterMembers(commonCoreGeneFiles[x[1]],
                                                                   commonCoreGeneFiles[x[2]]))

rm(temp)
#############################################################################################

#all the unique genes in the complete common core 

allCommon = c()

for(i in 1:5){
  
  tempDF = read.table(commonCoreGeneFiles[i], header = T, sep = ",", stringsAsFactors = F)
  
  allCommon <- c(allCommon, tempDF$x) 
  
}

uniqueAllCommon = unique(allCommon)

write.table(uniqueAllCommon, "uniqueAllCommon.txt", quote = F, row.names = F)
rm(allCommon,uniqueAllCommon, tempDF)

###############################################################################################



#compute average biomass per cluster

setwd("D:/BellosData/GF_3/BiomassFiles")

getAvgBiomassPerCluster<- function(clusterFile){
  
  clusterNo = unlist(str_extract_all(clusterFile, "\\d+"))[3]
  
  modelData = read.table(clusterFile,sep = ",", header = T, stringsAsFactors = F)
  
  modelNums = unlist(lapply(as.numeric(modelData$x), function(x) x + 1 ))
  
  biomassData = read.table("BiomassDF_Gen_140.txt",sep = ",", header = T, stringsAsFactors = F)
  
  temp = biomassData %>% filter(M %in% modelNums)
  
  noOfModels = nrow(temp)
  
  averageBiomass = mean(temp$biomassValue)
  
  minBiomass = min(temp$biomassValue)
    
  maxBiomass = max(temp$biomassValue)
  
  printStrin = paste0("ClusterNo_",clusterNo,";NoOfModels_",noOfModels,";AvgB_",averageBiomass,
                      ";MinB_",minBiomass,";MaxB_",maxBiomass)
  print(printStrin)
}

clusterFiles = list.files(pattern = "^Gen_\\d+_Biomass_hClustMemebers.*.txt$")

lapply(clusterFiles, function(x) getAvgBiomassPerCluster(x))


##################################################################################################

#get common inactive genes

setwd("D:/BellosData/GF_3/BiomassFiles")

getCommonInactiveGenes <- function(clusterFile, geneFile){
  
  generationNo = unlist(str_extract_all(geneFile, "\\d+"))[2]
  
  clusterNo = unlist(str_extract_all(clusterFile, "\\d+"))[3]
  
  modelData = read.table(clusterFile,sep = ",", header = T, stringsAsFactors = F)
  
  modelNums = unlist(lapply(as.numeric(modelData$x), function(x) x + 1 ))
  
  modelGenes = read.table(geneFile,header = T, sep = ",", stringsAsFactors = F)
  
  modelGenesCluster = modelGenes[,c("Gene",paste0("M_",modelNums))]
  
  commonInactiveCore = apply(modelGenesCluster[2:ncol(modelGenesCluster)], 1, function(x) all(x == 0))
  
  commonInactiveDF = modelGenesCluster %>% filter(commonInactiveCore)
  
  commonInactiveGenes = commonInactiveDF$Gene
  
  write.table(commonInactiveGenes, paste0("D:/BellosData/GF_3/BiomassFiles/CommonCoreGenes/CommonInactiveGenes_Gen_",
                                      generationNo,"_clusterNo_",clusterNo,".txt"), sep = ",", quote = F, row.names = F)
}

geneFile = "D:/BellosData/GC_3/Preprocessed/Generation_Chromosones_140_DF.txt"


clusterFiles = list.files(pattern = "^Gen_\\d+_Biomass_hClustMemebers.*.txt$")

lapply(clusterFiles, function(x) getCommonInactiveGenes(x,geneFile))

