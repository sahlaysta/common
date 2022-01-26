package com.github.sahlaysta.common.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicMenuItemUI;

/** A collection of cross-platform utility
 * methods for Swing JMenuItems.
 * @author porog */
public final class JMenuItemUtil {
	
	private JMenuItemUtil() {}
	
	/** Sets the accelerator text of a JMenuItem. The
	 * accelerator itself remains unaffected.
	 * A {@code null} string makes it so that the
	 * accelerator is not displayed on the JMenuItem.
	 * @param jmi the JMenuItem to be given
	 * custom accelerator text
	 * @param text the custom accelerator text to set
	 * to the JMenuItem */
	public static void setCustomAcceleratorText(JMenuItem jmi, String text) {
		//Get basicmenuitemui, must be basicmenuitemui
		ButtonUI ui = jmi.getUI();
		if (!(ui instanceof BasicMenuItemUI))
			return;
		BasicMenuItemUI miui = (BasicMenuItemUI)ui;

		//if the ui is not yet filtered, set the filter
		if (!(miui instanceof MenuItemUIFilter)) {
			MenuItemUIFilter miuif = new MenuItemUIFilter(miui);
			miuif.setAcceleratorText(text);
			jmi.setUI(miuif);
		//else modify the filter
		} else {
			((MenuItemUIFilter)miui).setAcceleratorText(text);
		}
		
		//reload the jmenuitem
		jmi.revalidate();
		jmi.repaint();
	}
	
	/** Returns the custom accelerator text of
	 * the specified JMenuItem. Returns {@code null} if
	 * {@link #setCustomAcceleratorText(JMenuItem, String)}
	 * was not previously called on the JMenuItem.
	 * @param jmi the JMenuItem with custom
	 * accelerator text
	 * @return the custom accelerator text
	 * of the JMenuItem, {@code null} if none */
	public static String getCustomAcceleratorText(JMenuItem jmi) {
		//get the jmenuitem's menuitemuifilter
		ButtonUI ui = jmi.getUI();
		if (!(ui instanceof MenuItemUIFilter))
			return null;
		MenuItemUIFilter miuif = (MenuItemUIFilter)ui;
		
		//returns the custom accelerator text
		return miuif.getAcceleratorText();
	}
	
	/** Removes custom accelerator text from
	 * a JMenuItem and reverts display behavior back
	 * to normal. Does nothing if
	 * {@link #setCustomAcceleratorText(JMenuItem, String)}
	 * was not previously called on the JMenuItem.
	 * @param jmi the JMenuItem with custom
	 * accelerator text */
	public static void removeCustomAcceleratorText(JMenuItem jmi) {
		//get the jmenuitem's menuitemuifilter
		ButtonUI ui = jmi.getUI();
		if (!(ui instanceof MenuItemUIFilter))
			return;
		MenuItemUIFilter miuif = (MenuItemUIFilter)ui;
		
		//revert jmenuitem back to normal
		jmi.setUI(miuif.bmiui);
		
		//reload the jmenuitem
		jmi.revalidate();
		jmi.repaint();
	}
	
	
	/* Class that extends basicmenuitemui, takes a basicmenuitemui object
	 * to back itself, and uses it to paint menu components. The
	 * code here is copied from java's official source code, however
	 * to access the private hidden fields, a lot of reflection had
	 * to be used */
	private static final class MenuItemUIFilter extends BasicMenuItemUI {
		
		//Constructor
		final BasicMenuItemUI bmiui;
		public MenuItemUIFilter(BasicMenuItemUI bmiui) {
			this.bmiui = bmiui;
			copyFields(bmiuiFields, bmiui, this);
		}
		
		//Reflection properties
		static final Class<?> bmiuiClass;
		static final Field[] bmiuiFields;
		static final Method applyInsets, isLeftToRight,
		useCheckAndArrow, layoutMenuItem, paintCheckIcon,
		paintIcon, paintText, paintAccText, paintArrowIcon,
		paintBackground, calcExtraWidths,
		calcWidthsAndHeights, setOriginalWidths,
		calcMaxWidths, calcMaxTextOffset,
		getLeadingGap, createMaxRect, getLeadingGap2,
		addMaxWidth, getCheckSize, getAfterCheckIconGap,
		isTopLevelMenu, getMinTextOffset,
		getLabelSize, getAccSize, getArrowSize, getGap,
		max, getHeight, getWidth, getMenuItem;
		static final Constructor<?> milhConstr;
		static final Field accText, leadingGap, viewRect,
		accelerator;
		static {//a lot of reflection
			try {
				bmiuiClass = Class.forName(
					"javax.swing.plaf.basic.BasicMenuItemUI");
				
				bmiuiFields = getFields(
					"acceleratorDelimiter",
					"acceleratorFont",
					"acceleratorForeground",
					"acceleratorSelectionForeground",
					"arrowIcon",
					"checkIcon",
					"defaultTextIconGap",
					"disabledForeground",
					"menuDragMouseListener",
					"menuItem",
					"menuKeyListener",
					"mouseInputListener",
					"oldBorderPainted",
					"propertyChangeListener",
					"selectionBackground",
					"selectionForeground");
				
				applyInsets = bmiuiClass.getDeclaredMethod(
					"applyInsets", Rectangle.class, Insets.class);
				applyInsets.setAccessible(true);
				
				//MenuItemLayoutHelper invis methods
				Class<?> milhClass = Class.forName(
					"sun.swing.MenuItemLayoutHelper");
				Class<?> lrClass = null;
				Class<?> rsClass = null;
				for (Class<?> c: milhClass.getDeclaredClasses()) {
					switch (c.getSimpleName()) {
					case "LayoutResult":
						lrClass = c;
						break;
					case "RectSize":
						rsClass = c;
						break;
					}
				}
				milhConstr = milhClass.getDeclaredConstructor(
					JMenuItem.class, Icon.class, Icon.class,
					Rectangle.class, int.class, String.class,
					boolean.class, Font.class, Font.class,
					boolean.class, String.class);
				milhConstr.setAccessible(true);
				useCheckAndArrow = milhClass.getDeclaredMethod(
					"useCheckAndArrow", JMenuItem.class);
				useCheckAndArrow.setAccessible(true);
				layoutMenuItem = milhClass.getDeclaredMethod(
					"layoutMenuItem");
				layoutMenuItem.setAccessible(true);
				accText = milhClass.getDeclaredField(
					"accText");
				accText.setAccessible(true);
				calcExtraWidths = milhClass.getDeclaredMethod(
					"calcExtraWidths");
				calcExtraWidths.setAccessible(true);
				calcWidthsAndHeights = milhClass.getDeclaredMethod(
					"calcWidthsAndHeights");
				calcWidthsAndHeights.setAccessible(true);
				setOriginalWidths = milhClass.getDeclaredMethod(
					"setOriginalWidths");
				setOriginalWidths.setAccessible(true);
				calcMaxWidths = milhClass.getDeclaredMethod(
					"calcMaxWidths");
				calcMaxWidths.setAccessible(true);
				calcMaxTextOffset = milhClass.getDeclaredMethod(
					"calcMaxTextOffset", Rectangle.class);
				calcMaxTextOffset.setAccessible(true);
				getLeadingGap = milhClass.getDeclaredMethod(
					"getLeadingGap", String.class);
				getLeadingGap.setAccessible(true);
				getLeadingGap2 = milhClass.getDeclaredMethod(
					"getLeadingGap");
				getLeadingGap2.setAccessible(true);
				leadingGap = milhClass.getDeclaredField(
					"leadingGap");
				leadingGap.setAccessible(true);
				viewRect = milhClass.getDeclaredField(
					"viewRect");
				viewRect.setAccessible(true);
				createMaxRect = milhClass.getDeclaredMethod(
					"createMaxRect");
				createMaxRect.setAccessible(true);
				addMaxWidth = milhClass.getDeclaredMethod(
					"addMaxWidth", rsClass, int.class, Dimension.class);
				addMaxWidth.setAccessible(true);
				getCheckSize = milhClass.getDeclaredMethod(
					"getCheckSize");
				getCheckSize.setAccessible(true);
				getAfterCheckIconGap = milhClass.getDeclaredMethod(
					"getAfterCheckIconGap");
				getAfterCheckIconGap.setAccessible(true);
				isTopLevelMenu = milhClass.getDeclaredMethod(
					"isTopLevelMenu");
				isTopLevelMenu.setAccessible(true);
				getMinTextOffset = milhClass.getDeclaredMethod(
					"getMinTextOffset");
				getMinTextOffset.setAccessible(true);
				getLabelSize = milhClass.getDeclaredMethod(
					"getLabelSize");
				getLabelSize.setAccessible(true);
				getAccSize = milhClass.getDeclaredMethod(
					"getAccSize");
				getAccSize.setAccessible(true);
				getArrowSize = milhClass.getDeclaredMethod(
					"getArrowSize");
				getArrowSize.setAccessible(true);
				getGap = milhClass.getDeclaredMethod(
					"getGap");
				getGap.setAccessible(true);
				max = milhClass.getDeclaredMethod(
					"max", int[].class);
				max.setAccessible(true);
				getHeight = rsClass.getDeclaredMethod(
					"getHeight");
				getHeight.setAccessible(true);
				getWidth = rsClass.getDeclaredMethod(
					"getWidth");
				getWidth.setAccessible(true);
				getMenuItem = milhClass.getDeclaredMethod(
					"getMenuItem");
				getMenuItem.setAccessible(true);
				
				//BasicMenuItemUI invis methods
				paintCheckIcon = bmiuiClass.getDeclaredMethod(
					"paintCheckIcon", Graphics.class, milhClass,
					lrClass, Color.class, Color.class);
				paintCheckIcon.setAccessible(true);
				paintIcon = bmiuiClass.getDeclaredMethod(
					"paintIcon", Graphics.class, milhClass,
					lrClass, Color.class);
				paintIcon.setAccessible(true);
				paintText = bmiuiClass.getDeclaredMethod(
					"paintText", Graphics.class,
					milhClass, lrClass);
				paintText.setAccessible(true);
				paintAccText = bmiuiClass.getDeclaredMethod(
					"paintAccText", Graphics.class,
					milhClass, lrClass);
				paintAccText.setAccessible(true);
				paintArrowIcon = bmiuiClass.getDeclaredMethod(
					"paintArrowIcon", Graphics.class,
					milhClass, lrClass, Color.class);
				paintArrowIcon.setAccessible(true);
				paintBackground = bmiuiClass.getDeclaredMethod(
					"paintBackground", Graphics.class,
					JMenuItem.class, Color.class);
				paintBackground.setAccessible(true);
				
				//BasicGraphicsUtils
				isLeftToRight = BasicGraphicsUtils.class
					.getDeclaredMethod(
						"isLeftToRight", Component.class);
				isLeftToRight.setAccessible(true);
				
				//JMenuItem
				accelerator = JMenuItem.class
					.getDeclaredField(
						"accelerator");
				accelerator.setAccessible(true);
			} catch (Exception e) {
				throw new InternalError(e);
			}
		}
		private static Field[] getFields(String... fields)
				throws NoSuchFieldException, SecurityException {
			Field[] result = new Field[fields.length];
			for (int i = 0; i < fields.length; i++) {
				result[i] = bmiuiClass.getDeclaredField(
					fields[i]);
				result[i].setAccessible(true);
			}
			return result;
		}
		
		//Copies the fields from one object to another
		private static void copyFields(
				Field[] fields, Object o1, Object o2) {
			try {
				for (Field field: fields)
					field.set(o2, field.get(o1));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		private void uFields() {
			copyFields(bmiuiFields, this, bmiui);
		}
		
		
		//unchecks invocation exceptions
		private static Object
		doUnchecked(Method m, Object obj, Object... args) {
			try {
				return m.invoke(obj, args);
			} catch (Exception e) {
				throw new InternalError(e);
			}
		}
		private static Object
		doUnchecked(Constructor<?> c, Object... initargs) {
			try {
				return c.newInstance(initargs);
			} catch (Exception e) {
				throw new InternalError(e);
			}
		}
		private static void
		doUnchecked(Field field, Object obj, Object value) {
			try {
				field.set(obj, value);
			} catch (Exception e) {
				throw new InternalError(e);
			}
		}
		private static Object
		doUnchecked(Field field, Object obj) {
			try {
				return field.get(obj);
			} catch (Exception e) {
				throw new InternalError(e);
			}
		}
		
		//Get/set the accelerator text
		String acceleratorText;
		void setAcceleratorText(String text) {
			acceleratorText = text == null ? "" : text;
		}
		String getAcceleratorText() {
			return acceleratorText;
		}
		
		
		//Override paint methods
		/* Taken from java's source code @ BasicMenuItemUI.java
		 * Uses reflection to access the hidden fields that
		 * it uses */
		@Override
		protected void paintBackground(
				Graphics g, JMenuItem menuItem,
				Color bgColor) {
			uFields();
			
			doUnchecked(paintBackground, bmiui,
				g, menuItem, bgColor);
		}
		@Override
		protected void paintMenuItem(
				Graphics g, JComponent c, Icon checkIcon,
				Icon arrowIcon, Color background,
				Color foreground, int defaultTextIconGap) {
			uFields();
			
			// Save original graphics font and color
			Font holdf = g.getFont();
			Color holdc = g.getColor();
			
			JMenuItem mi = (JMenuItem) c;
			g.setFont(mi.getFont());
			
			//Temporarily set jmenuitem accelerator to null
			//(for width calculation)
			Object a = mi.getAccelerator();
			doUnchecked(accelerator, mi, null);
			
			Rectangle vr = new Rectangle(0, 0, mi.getWidth(), mi.getHeight());
			doUnchecked(applyInsets, bmiui, vr, mi.getInsets());
			
			//get menuitemlayouthelper
			Object lh = doUnchecked(milhConstr,
				mi, checkIcon, arrowIcon, vr,
				defaultTextIconGap, acceleratorDelimiter,
				doUnchecked(isLeftToRight, null, mi),
				mi.getFont(), acceleratorFont,
				doUnchecked(useCheckAndArrow, null, menuItem),
				getPropertyPrefix());
			
			//set accelerator
			doUnchecked(accText, lh, acceleratorText);
			
			//recalculate dimensions
			doUnchecked(calcExtraWidths, lh);
			doUnchecked(calcWidthsAndHeights, lh);
			doUnchecked(setOriginalWidths, lh);
			doUnchecked(calcMaxWidths, lh);
			doUnchecked(leadingGap, lh,
				doUnchecked(getLeadingGap, lh, getPropertyPrefix()));
			doUnchecked(calcMaxTextOffset, lh,
				doUnchecked(viewRect, lh));

			//layoutresult
			Object lr = doUnchecked(layoutMenuItem, lh);
			
			//paint
			paintBackground(g, mi, background);
			doUnchecked(paintCheckIcon, bmiui, g,
				lh, lr, holdc, foreground);
			doUnchecked(paintIcon, bmiui, g,
				lh, lr, holdc);
			doUnchecked(paintText, bmiui, g,
				lh, lr);
			doUnchecked(paintAccText, bmiui, g,
				lh, lr);
			doUnchecked(paintArrowIcon, bmiui, g,
				lh, lr, foreground);
			
			// Restore original graphics font and color
			g.setColor(holdc);
			g.setFont(holdf);
			
			//Restore jmenuitem accelerator
			doUnchecked(accelerator, mi, a);
		}
		@Override
		protected Dimension getPreferredMenuItemSize(
				JComponent c, Icon checkIcon,
				Icon arrowIcon, int defaultTextIconGap) {
			uFields();
			
			JMenuItem mi = (JMenuItem) c;
			
			//Temporarily set jmenuitem accelerator to null
			//(for width calculation)
			Object a = mi.getAccelerator();
			doUnchecked(accelerator, mi, null);
			
			//get menuitemlayouthelper
			Object lh = doUnchecked(milhConstr,
				mi, checkIcon, arrowIcon,
				doUnchecked(createMaxRect, null),
				defaultTextIconGap, acceleratorDelimiter,
				doUnchecked(isLeftToRight, null, mi),
				mi.getFont(), acceleratorFont,
				doUnchecked(useCheckAndArrow, null, menuItem),
				getPropertyPrefix());
			
			//set accelerator
			doUnchecked(accText, lh, acceleratorText);
			
			//recalculate dimensions
			doUnchecked(calcExtraWidths, lh);
			doUnchecked(calcWidthsAndHeights, lh);
			doUnchecked(setOriginalWidths, lh);
			doUnchecked(calcMaxWidths, lh);
			doUnchecked(leadingGap, lh,
				doUnchecked(getLeadingGap, lh, getPropertyPrefix()));
			doUnchecked(calcMaxTextOffset, lh,
				doUnchecked(viewRect, lh));
			
			Dimension result = new Dimension();
			
			// Calculate the result width
			result.width = (int)doUnchecked(getLeadingGap2, lh);
			doUnchecked(
				addMaxWidth,
				null,
				doUnchecked(getCheckSize, lh),
				doUnchecked(getAfterCheckIconGap, lh),
				result);
			// Take into account mimimal text offset.
			boolean isTLM = (boolean)doUnchecked(isTopLevelMenu, lh);
			int mto = (int)doUnchecked(getMinTextOffset, lh);
			if (!isTLM && mto > 0 && result.width < mto)
				result.width = mto;
			doUnchecked(
				addMaxWidth,
				null,
				doUnchecked(getLabelSize, lh),
				doUnchecked(getGap, lh),
				result);
			doUnchecked(
				addMaxWidth,
				null,
				doUnchecked(getAccSize, lh),
				doUnchecked(getGap, lh),
				result);
			doUnchecked(
				addMaxWidth,
				null,
				doUnchecked(getArrowSize, lh),
				doUnchecked(getGap, lh),
				result);
			
			// Calculate the result height
			result.height = (int)doUnchecked(
				max,
				null,
				new int[] {
					(int)doUnchecked(
						getHeight,
						doUnchecked(getCheckSize, lh)),
					(int)doUnchecked(
						getHeight,
						doUnchecked(getLabelSize, lh)),
					(int)doUnchecked(
						getHeight,
						doUnchecked(getArrowSize, lh))
				});
			
			// Take into account menu item insets
			Insets insets = ((JMenuItem)doUnchecked(
				getMenuItem, lh)).getInsets();
			if (insets != null) {
				result.width += insets.left + insets.right;
				result.height += insets.top + insets.bottom;
			}
			
			// if the width is even, bump it up one. This is critical
			// for the focus dash line to draw properly
			if (result.width % 2 == 0) {
				result.width++;
			}
			
			// if the height is even, bump it up one. This is critical
			// for the text to center properly
			if (result.height % 2 == 0
				&& Boolean.TRUE != UIManager.get(
					getPropertyPrefix() + ".evenHeight")) {
				result.height++;
			}
			
			//Restore jmenuitem accelerator
			doUnchecked(accelerator, mi, a);
			
			return result;
		}
	}
}