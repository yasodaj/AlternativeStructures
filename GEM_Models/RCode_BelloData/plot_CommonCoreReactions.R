#plot common core in a heat map

setwd("D:/BellosData/GF_3/Biomass_Analysis/Gen_140/CommonCoreReactions")

processPath = "D:/BellosData/GF_3/Biomass_Analysis/Gen_140/CommonCoreReactions/"

generationNo = 140

getReactions <- function(model){
  
  modelData = read.table(model, sep = ",", header = T, stringsAsFactors = F)
  
  return(modelData[,1])
  
}

commonCoreFiles = gtools::mixedsort(list.files(pattern = "^CommonCoreReactions_Gen_\\d+_clusterNo_\\d+.txt$"))

allTheReactions = Reduce(union, lapply(commonCoreFiles, function(x) getReactions(x)))

allTheReactions <- allTheReactions[!is.na(allTheReactions)]

reactionBinaryDF <- as.data.frame(matrix(0,ncol = (length(commonCoreFiles) + 1), nrow = length(allTheReactions)))

colnames(reactionBinaryDF) <- c("Reaction", paste0("Cluster_",1:length(commonCoreFiles)))

reactionBinaryDF$Reaction <- allTheReactions


for(i in 1:length(commonCoreFiles)){

  clusterNo = unlist(str_extract_all(commonCoreFiles[i],"\\d+"))[2]
  coreR = read.table(commonCoreFiles[i],header = T, stringsAsFactors = F)
  reactionBinaryDF[,paste0("Cluster_",clusterNo)] <- ifelse(reactionBinaryDF$Reaction %in% coreR[,1], 1, 0)
  
}


reactionBinaryDFMelt = reshape2::melt(reactionBinaryDF, id.vars = "Reaction")
names(reactionBinaryDFMelt)[2:3] <- c("ClusterID", "IsPresent")

yAxisOrder <- mixedsort(reactionBinaryDFMelt$Reaction)


png(paste0(processPath,"Gen_",generationNo,"_reactionCommonCore.png"), width = 1200, height = 600)

ggplot(data = reactionBinaryDFMelt, aes(x = ClusterID, y = factor(Reaction, level = yAxisOrder))) +
  geom_tile(aes(fill = IsPresent), color = "white") +
  scale_fill_gradient(low = "white", high = "steelblue") +
  ylab("Common core reactions") +
  xlab("Clusters") +
  ggtitle(paste0("Generation ",generationNo," reaction common core across clusters"))+
  theme(plot.title = element_text(size=12),
        axis.title=element_text(size=10),
        axis.text.x = element_text(size = 10),
        axis.text.y = element_text(size = 8),
        legend.position = "none")

dev.off()
