#get the common core reactions for each cluster

setwd("D:/BellosData/GF_3/Biomass_Analysis/Gen_140")

clusterFiles = gtools::mixedsort(list.files(pattern = "^Gen_\\d+_Biomass_hClustMemebers_.*.txt$"))
generationNo = 140

getModelNos <- function(v) {
  pat <- paste0("(", paste0(v, collapse="|"), ")")
  return(pat)
}

getCommonReactions <- function(model){
  
  modelData = read.table(paste0("D:/BellosData/GF_3/Models/",model), sep = ",", header = T, stringsAsFactors = F)
  
  filteredModelData = modelData[(modelData$Reaction != "Biomass") & (modelData$Activity > 0.0000),]
  
  return(filteredModelData$Reaction)
  
}

getCommonCoreReactions<- function(clusterFile){
  
  clusterNo = as.numeric(unlist(str_extract_all(clusterFile,"\\d+"))[3])
  clusterData = read.table(clusterFile, sep = ",", header = T, stringsAsFactors = F)
  
  
  pat <- paste0("^Generation_Flux_",generationNo,"_Model_", 
                getModelNos(clusterData[,1]), ".txt$")
  reactionFiles = mixedsort(list.files(path = "D:/BellosData/GF_3/Models/" ,pattern = pat))
  
  commonCoreR = Reduce(intersect, lapply(reactionFiles, function(x) getCommonReactions(x)))
  
  commonCoreR <- append(length(commonCoreR), commonCoreR)
  
  
  write.table(commonCoreR, 
              paste0("D:/BellosData/GF_3/Biomass_Analysis/Gen_",generationNo,"/CommonCoreReactions_Gen_",
              generationNo,"_clusterNo_",clusterNo,".txt"), sep = "\n", quote = F, row.names = F, col.names = F)
  
}

lapply(clusterFiles, function(x) getCommonCoreReactions(x))

##############################################################################################
















#get the common of common cores

setwd("D:/BellosData/GF_3/BiomassFiles/CommonCoreReactions")
commonCoreFiles = list.files(pattern = "^CommonCoreReactions_Gen_.*.txt$")

for(i in 1:5){
  
  tempDF = read.table(commonCoreFiles[i], header = T, sep = ",", stringsAsFactors = F)
  
  if(i == 1){
    
    commonOfCommon = tempDF[,1]
    
  }else{
    commonOfCommon = intersect(commonOfCommon, tempDF[,1])
  }
  
}

commonOfCommon <- append(length(commonOfCommon), commonOfCommon)

write.table(commonOfCommon, "CommonOfCommonR.txt", quote = F, row.names = F, col.names = F)

###############################################################################################

#difference in cores


#get the difference in each comparison

setwd("D:/BellosData/GF_3/BiomassFiles/CommonCoreReactions")

geneSetDifferenceClusterMembers<- function(cluster1, cluster2){
  
  cluster1No = as.numeric(unlist(str_extract_all(cluster1, "\\d+"))[2])
  cluster2No = as.numeric(unlist(str_extract_all(cluster2, "\\d+"))[2])
  
  cluster1Data = read.table(cluster1, header = T, sep = ",", stringsAsFactors = F)
  cluster2Data = read.table(cluster2, header = T, sep = ",", stringsAsFactors = F)
  
  temp = data.frame(Clust1 = cluster1No, Clust2 = cluster2No, 
                    Set_diff_genes = setdiff(cluster1Data[,1], cluster2Data[,1]), stringsAsFactors = F)

  write.table(temp,
              paste0("D:/BellosData/GF_3/BiomassFiles/CommonCoreReactions/Gen_140_clusters_",
                     cluster1No,"_",cluster2No,"_setDiff_reactions.txt"),
              sep = ",", quote = F, row.names = F)

  
}

rowCombination = split(v<-t(utils::combn(2, 2)), seq(nrow(v))); rm(v)

commonCreReactionFiles = list.files(pattern = "^CommonCoreReactions_Gen_.*.txt$")

lapply(rowCombination, function(x) geneSetDifferenceClusterMembers(commonCreReactionFiles[x[1]],
                                                                   commonCreReactionFiles[x[2]]))
