#plot the gene activity within model

setwd("D:/BellosData/GC_3/Clustering_activePercentage")


plotActivity <- function(generation){
  
  genNo = as.numeric(str_extract(generation, "\\d+"))
  
  activityData = read.table(generation, sep = ",", header = T, stringsAsFactors = F)
  
  activityData$Model = factor(activityData$Model, levels = gtools::mixedsort(activityData$Model))
  
  plotB<- ggplot(data=activityData, aes(x=Model, y=ActivePercent, group=1)) +
    geom_line(colour = "steel blue")+
    geom_point(colour = "steel blue", size = 2) +
    labs(title=paste0("Generation ",genNo," Gene Activity (Within model)"),x="Model", y = "Activity") + 
    theme_minimal() + 
    theme(axis.text.x = element_text(angle = 90, hjust = 1, size = 8)) + 
    ylim(0,1)
  
  ggsave(paste0("D:/BellosData/GC_3/Clustering_activePercentage/scatterPlot/GeneActivityWithinModel_Gen_",genNo,".png"), plotB,
         width = 16, height = 8, dpi = 250)
  
  
}

activityFiles = list.files(pattern = "^Gen_\\d+_activePercentage.txt")


lapply(activityFiles, function(x) plotActivity(x))


#####################################################################################################


plotActivityAndBiomass <- function(generation, biomass){
  
  genNo = as.numeric(str_extract(generation, "\\d+"))
  
  activityData = read.table(generation, sep = ",", header = T, stringsAsFactors = F)
  
  biomassData = read.table(paste0("D:/BellosData/GF_3/Plot_Biomass/",biomass), sep = ",", header = T, stringsAsFactors = F)
  
  activityData = cbind(activityData, biomass = round(as.numeric(as.character(biomassData$biomass_metric)), digits = 5))
  
  reshaped = reshape2::melt(activityData, id = c("Model"))
  
  reshaped$Model = factor(reshaped$Model, levels = gtools::mixedsort(reshaped$Model))
  
  plotB<- ggplot(data=reshaped, aes(x=Model, y=value, group=variable)) +
    geom_line(aes(color=variable))+
    geom_point(aes(color=variable)) +
    labs(title=paste0("Generation ",genNo," Gene Activity (Within model) and Biomass"),x="Model", y = "Activity") + 
    theme_minimal() + 
    theme(axis.text.x = element_text(angle = 90, hjust = 1, size = 8)) + 
    ylim(0,2) +
    scale_color_brewer(palette="Dark2")
  
  ggsave(paste0("D:/BellosData/GC_3/Clustering_activePercentage/activity_biomass/GeneActivity_biomass_Gen_",genNo,".png"), plotB,
         width = 16, height = 8, dpi = 250)
  
  rm(activityData, biomassData)
}

activityFiles = gtools::mixedsort(list.files(pattern = "^Gen_\\d+_activePercentage.txt"))

biomassFiles = gtools::mixedsort(list.files(path = "D:/BellosData/GF_3/Plot_Biomass",
                                            pattern = "^Generation_\\d+_biomass.txt$"))

rowCombination = split(v<-t(utils::combn(450, 1)), seq(nrow(v))); rm(v)

lapply(rowCombination, function(x) plotActivityAndBiomass(activityFiles[x],biomassFiles[x]))

