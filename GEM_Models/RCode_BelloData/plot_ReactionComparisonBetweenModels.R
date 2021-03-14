#plot common core in a heat map

setwd("D:/BellosData/GF_3/Models")

processPath = "D:/BellosData/GF_3/Biomass_Analysis/"

generationNo = 45

models = c(1,2)

getModelNo <- function(v) {
  pat <- paste0("(", paste0(v, collapse="|"), ")")
  return(pat)
}

getReactions <- function(model){

  modelData = read.table(model, sep = ",", header = T, stringsAsFactors = F)
  modelData = modelData[modelData$Reaction !='Biomass', ]
  modelData$Activity = as.numeric(as.character(modelData$Activity))
  
  #return(modelData$Reaction[modelData$Activity>0.00001])
  
  return(modelData$Reaction)
  
}

commonCoreFiles = gtools::mixedsort(list.files(pattern = "^CommonCoreReactions_Gen_\\d+_clusterNo_\\d+.txt$"))

pat <- paste0("^Generation_Flux_", generationNo, "_Model_",getModelNo(models),".txt$")

modelFiles = gtools::mixedsort(list.files(pattern = pat))

allTheReactions = Reduce(union, lapply(modelFiles, function(x) getReactions(x)))

allTheReactions <- allTheReactions[!is.na(allTheReactions)]

reactionBinaryDF <- as.data.frame(matrix(0,ncol = (length(modelFiles) + 1), 
                                         nrow = length(allTheReactions)))

colnames(reactionBinaryDF) <- c("Reaction", paste0("Model_",models))

reactionBinaryDF$Reaction <- allTheReactions


for(i in 1:length(modelFiles)){
  
  modelNo = unlist(str_extract_all(modelFiles[i],"\\d+"))[2]
  modelData = read.table(modelFiles[i], sep = ",", header = T, stringsAsFactors = F)
  modelData = modelData[modelData$Reaction !='Biomass', ]
  modelData$Activity = as.numeric(as.character(modelData$Activity))
  
  #reactions = modelData$Reaction[modelData$Activity>0.00001]
  reactions = modelData$Reaction
  
  reactionBinaryDF[,paste0("Model_",modelNo)] <- ifelse(reactionBinaryDF$Reaction %in% reactions, 1, 0)
  
}


reactionBinaryDFMelt = reshape2::melt(reactionBinaryDF, id.vars = "Reaction")
names(reactionBinaryDFMelt)[2:3] <- c("ModelNo", "IsPresent")

yAxisOrder <- mixedsort(reactionBinaryDFMelt$Reaction)

modelNumbers = paste(models, collapse = "_")

png(paste0(processPath,"Gen_",generationNo,"_reactionComparison_models(",modelNumbers,")noFilter.png"), 
    width = 1200, height = 600)

# png(paste0(processPath,"Gen_",generationNo,"_reactionComparison_models(",modelNumbers,").png"), 
#     width = 1200, height = 600)

ggplot(data = reactionBinaryDFMelt, aes(x = ModelNo, y = factor(Reaction, level = yAxisOrder))) +
  geom_tile(aes(fill = IsPresent), color = "white") +
  scale_fill_gradient(low = "white", high = "steelblue") +
  ylab("Reaction comparison") +
  xlab("Models") +
  ggtitle(paste0("Generation ",generationNo," reaction comparison across models (",modelNumbers,") no filter"))+
  #ggtitle(paste0("Generation ",generationNo," reaction comparison across models (",modelNumbers,")"))+
  theme(plot.title = element_text(size=12),
        axis.title=element_text(size=10),
        axis.text.x = element_text(size = 10),
        axis.text.y = element_text(size = 8),
        legend.position = "none")

dev.off()
