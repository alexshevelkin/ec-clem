/**
 * Copyright 2010-2018 Perrine Paul-Gilloteaux <Perrine.Paul-Gilloteaux@univ-nantes.fr>, CNRS.
 * Copyright 2019 Guillaume Potier <guillaume.potier@univ-nantes.fr>, INSERM.
 *
 * This file is part of EC-CLEM.
 *
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 **/

/**
 * AUthor: Perrine.Paul-Gilloteaux@univ-nantes.fr
 * Main Class can be used alone or call from another plugin:
 * will apply the transform described in an storage file produced by ec-clem to an ICY ROI.
 * It only takes the storage file and the destinatin file as input and the source file with the ROIs to be transformed.
 */
package plugins.perrine.ec_clem.ec_clem.misc;
import java.awt.geom.Point2D;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import Jama.Matrix;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzVarFile;
import plugins.adufour.ezplug.EzVarSequence;
import plugins.adufour.vars.lang.VarROIArray;
import plugins.perrine.ec_clem.ec_clem.storage.transformation.xml.XmlFiletoTransformationReader;
import icy.canvas.IcyCanvas;
import icy.canvas.IcyCanvas2D;

import icy.gui.dialog.MessageDialog;
import icy.gui.frame.progress.AnnounceFrame;
import icy.gui.frame.progress.ProgressFrame;


import icy.preferences.ApplicationPreferences;
import icy.roi.ROI;
import icy.sequence.Sequence;

import icy.system.thread.ThreadUtil;
import icy.type.geom.Polygon2D;
import icy.type.point.Point5D;
import icy.util.XMLUtil;
import plugins.adufour.blocks.lang.Block;
import plugins.adufour.blocks.util.VarList;
import plugins.adufour.ezplug.EzLabel;



public class ApplyTransformationtoRoi extends EzPlug implements Block{

	EzVarSequence source=new EzVarSequence("Select Source Image (showing Rois)");
	EzVarSequence target=new EzVarSequence("Select Target Image (to set the new Rois)");
	private EzVarFile xmlFile=new EzVarFile("Xml file containing ec-clem v2 transformation", ApplicationPreferences.getPreferences().node("frame/imageLoader").get("path", "."));;

	private Runnable transformer;

	protected VarROIArray outputROIs = new VarROIArray("list of ROI");
	private int auto;


	@Override
	protected void initialize() {
	// 	TODO Auto-generated by Icy4Eclipse
		EzLabel textinfo=new EzLabel("Please open images with Roi (source) and the destination target image, and the storage file containing the transformations (likely your source file name _transfo.storage)");

		String varName ="Xml file containing ec-clem v2 transformation";
		if (source.getValue()!=null)
			xmlFile=new EzVarFile(varName, source.getValue().getFilename());
		else
			xmlFile=new EzVarFile(varName, ApplicationPreferences.getPreferences().node("frame/imageLoader").get("path", "."));

		addEzComponent(textinfo);
		addEzComponent(source);
		addEzComponent(target);
		addEzComponent(xmlFile);
	}
/**
 * play
 */
	@Override
	protected void execute() {


		final Sequence sourceseq=source.getValue();
		//Icy.getMainInterface().addActiveSequenceListener(this);
		//String name=sourceseq.getFilename()+"_transfo.storage";
		if (sourceseq==null){
			MessageDialog.showDialog("Please make sure that your image is opened");
			return;
		}

		final Document document = XMLUtil.loadDocument( xmlFile.getValue());

		transformer = new Runnable() {
	        @Override
	        public void run()
	        {
		

		
	        	Matrix CombinedTransfo=getTransfov2(document);
		
		
	        	ProgressFrame progress = new ProgressFrame("Applying transformation...");
	        	ArrayList<ROI> Rois = sourceseq.getROIs();
	        	for (ROI Roi : Rois){

	        		ROI newRoi=Roi.getCopy();
	        		switch(newRoi.getSimpleClassName()){
	        			case "ROI2DRectangle":
	        				Point5D oldposition = Roi.getPosition5D();
	        				Point5D newposition=transformPoints5D(CombinedTransfo,oldposition);
	        				//newposition.setZ(newpositionmatrix.get(0,2));
	        				newRoi.setPosition5D(newposition);
	        				if (!isHeadLess()){
	        					target.getValue().addROI(newRoi);
	        				}
	        				else{
	        					outputROIs.add(newRoi);
					}
					break;
	        			case "ROI2DEllipse":
	        				Point5D oldposition2 = Roi.getPosition5D();
	        				Point5D newposition2=transformPoints5D(CombinedTransfo,oldposition2);
	        				//newposition.setZ(newpositionmatrix.get(0,2));
	        				newRoi.setPosition5D(newposition2);
	        				ArrayList<Point2D> ControlPoints = ((plugins.kernel.roi.roi2d.ROI2DEllipse) newRoi).getPoints();

	        				for (Point2D pt2D:ControlPoints){
	        					oldposition2 = new Point5D.Double(pt2D.getX(),pt2D.getY(),1,1,1);
	        					newposition2=transformPoints5D(CombinedTransfo,oldposition2);
					
	        					pt2D.setLocation(newposition2.getX(), newposition2.getY());
	        				}
	        				newRoi.roiChanged(true);
	        				if (!isHeadLess()){
	        					target.getValue().addROI(newRoi);
	        				}
	        				else{
	        					outputROIs.add(newRoi);
	        				}
	        			break;
	        			case "ROI2DPolygon":
	        				
	        				Point5D oldposition3 = Roi.getPosition5D();
	        				Point5D newposition3=transformPoints5D(CombinedTransfo,oldposition3);
					

	        				Polygon2D poly = ((plugins.kernel.roi.roi2d.ROI2DPolygon) newRoi).getPolygon2D();

	        				for (int i=0; i<poly.npoints;i++){
	        					oldposition3 = new Point5D.Double(poly.xpoints[i],poly.ypoints[i],1,1,1);
	        					newposition3=transformPoints5D(CombinedTransfo,oldposition3);
	        					//newposition.setZ(newpositionmatrix.get(0,2));
	        					poly.xpoints[i]=newposition3.getX();
	        					poly.ypoints[i]=newposition3.getY();
	        					
	        				}
	        				((plugins.kernel.roi.roi2d.ROI2DPolygon) newRoi).setPolygon2D(poly);
	        				newRoi.roiChanged(true);
	        				if (!isHeadLess()){
	        					target.getValue().addROI(newRoi);
	        				}
	        				else{
	        					outputROIs.add(newRoi);
	        				}
	        				break;
	        			default:
	        				System.err.println("Roi of type "+ newRoi.getSimpleClassName()+" non implemented yet ");
	        				break;
	        		}



	        	}
			progress.close();
		
		
		if (!isHeadLess()){
		IcyCanvas sourcecanvas = source.getValue().getFirstViewer().getCanvas();
		if (sourcecanvas instanceof IcyCanvas2D)
			((IcyCanvas2D) sourcecanvas).fitCanvasToImage();
		}


	}
		};
		if (!this.isHeadLess()){
		ThreadUtil.bgRun(transformer);
		}
		else{
		ThreadUtil.invokeNow(transformer);
		}

	}

	protected Point5D transformPoints5D(Matrix combinedTransfo, Point5D oldposition) {
		// double[][] array = {{oldposition.getX()*source.getValue().getPixelSizeX(),oldposition.getY()*source.getValue().getPixelSizeY(),oldposition.getZ()*source.getValue().getPixelSizeZ(),1}};
		 double[][] array = {{oldposition.getX()*source.getValue().getPixelSizeX(),oldposition.getY()*source.getValue().getPixelSizeY(),1,1}};
		    Matrix oldpositionmatrix= new Matrix(array);
		    Matrix newpositionmatrix=combinedTransfo.times(oldpositionmatrix.transpose());
		    Point5D newposition = new Point5D.Double();
			newposition.setX(newpositionmatrix.get(0,0)/target.getValue().getPixelSizeX());
			newposition.setY(newpositionmatrix.get(1,0)/target.getValue().getPixelSizeY());
			//newposition.setZ(newpositionmatrix.get(2,0)/target.getValue().getPixelSizeZ());
			newposition.setZ(1.0);
			return newposition;
	}


	/**
	 * not used
	 */
	@Override
	public void clean() {
		// TODO Auto-generated by Icy4Eclipse
	}
	/**
	 * compute (again) the combine transformed to avoid interpolation arrors
	 * @param document
	 * @return
	 */
	public Matrix getCombinedTransfo(Document document){
		Element root = XMLUtil.getRootElement(document);




		ArrayList<Element> transfoElementArrayList = XMLUtil.getElements( root , "MatrixTransformation" );

		ArrayList<Matrix> listoftransfo=new ArrayList<Matrix>();
		for ( Element transfoElement : transfoElementArrayList )
		{
			double[][] m=new double[4][4];


			m[0][0] = XMLUtil.getAttributeDoubleValue(  transfoElement, "m00" , 0 );
			m[0][1] = XMLUtil.getAttributeDoubleValue(  transfoElement, "m01" , 0 );
			m[0][2] = XMLUtil.getAttributeDoubleValue(  transfoElement, "m02" , 0 );
			m[0][3] = XMLUtil.getAttributeDoubleValue(  transfoElement, "m03" , 0 );

			m[1][0] = XMLUtil.getAttributeDoubleValue(  transfoElement, "m10" , 0 );
			m[1][1] = XMLUtil.getAttributeDoubleValue(  transfoElement, "m11" , 0 );
			m[1][2]= XMLUtil.getAttributeDoubleValue(  transfoElement, "m12" , 0 );
			m[1][3] = XMLUtil.getAttributeDoubleValue(  transfoElement, "m13" , 0 );

			m[2][0]= XMLUtil.getAttributeDoubleValue(  transfoElement, "m20" , 0 );
			m[2][1] = XMLUtil.getAttributeDoubleValue(  transfoElement, "m21" , 0 );
			m[2][2] = XMLUtil.getAttributeDoubleValue(  transfoElement, "m22" , 0 );
			m[2][3] = XMLUtil.getAttributeDoubleValue(  transfoElement, "m23" , 0 );

			m[3][0] = XMLUtil.getAttributeDoubleValue(  transfoElement, "m30" , 0 );
			m[3][1] = XMLUtil.getAttributeDoubleValue(  transfoElement, "m31" , 0 );
			m[3][2] = XMLUtil.getAttributeDoubleValue(  transfoElement, "m32" , 0 );
			m[3][3] = XMLUtil.getAttributeDoubleValue(  transfoElement, "m33" , 0 );


			Matrix T=new Matrix(m);
			listoftransfo.add(T);


		}
		Matrix CombinedTransfo=Matrix.identity(4, 4);
		for (int i=0;i<listoftransfo.size();i++){
			CombinedTransfo=listoftransfo.get(i).times(CombinedTransfo);
		}
		return CombinedTransfo;
	}
	
	/**
	 * compute (again) the combine transformed to avoid interpolation arrors
	 * @param document
	 * @return
	 */
	public Matrix getTransfov2(Document document){
		XmlFiletoTransformationReader reader=new XmlFiletoTransformationReader();
		double[][] m=reader.read(document);
		Matrix T=new Matrix(m);

		return T;
	}
	/**
	 * get the storage file
	 * @return
	 */
	public Document getdocumentTitle() {
		Document document = XMLUtil.loadDocument( xmlFile.getValue());
		return document;
	}
	@Override
	public void declareInput(VarList inputMap) {
		// TODO Auto-generated method stub
		inputMap.add("Input Image",source.getVariable());
		inputMap.add("Imput XML File",xmlFile.getVariable());

	}
	@Override
	public void declareOutput(VarList outputMap) {
		// TODO Auto-generated method stub
		 outputMap.add("output transformedregions", outputROIs);
	}
}
