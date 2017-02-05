using System.Windows;
using System.Windows.Input;
using Genetec.Sdk.Workspace.Maps;

// ==========================================================================
// Copyright (C) 2015 by Genetec, Inc.
// All rights reserved.
// May be used only in accordance with a valid Source Code License Agreement.
//
// Ephemerides for October 22:
//  1575 – Foundation of Aguascalientes.
//  1730 – Construction of the Ladoga Canal is completed.
//  1975 – The Soviet unmanned space mission Venera 9 lands on Venus.
// ==========================================================================
namespace DynamicMapObjects.Maps
{
    #region Classes

    /// <summary>
    /// Interaction logic for OfficerMapObjectView.xaml
    /// </summary>
    public partial class OfficerMapObjectView
    {
        #region Constants

        public const string OfficerLayerName = "Officers";

        #endregion

        public bool m_canDragDrop = false;

        #region Constructors

        public OfficerMapObjectView()
        {
            InitializeComponent();
        }

        #endregion

        private void OnOfficerMapObjectViewMouseDown(object sender, MouseButtonEventArgs e)
        {
            m_canDragDrop = true;
        }

        private void OnOfficerMapObjectViewMouseMove(object sender, MouseEventArgs e)
        {
            MapObjectView officerMapObject = sender as MapObjectView;
            if (officerMapObject != null && m_canDragDrop && e.LeftButton == MouseButtonState.Pressed)
            {
                if (DragDrop.DoDragDrop(this, officerMapObject.MapObject.LinkedEntity, DragDropEffects.Copy) == DragDropEffects.None)
                {
                    m_canDragDrop = false;
                }
            }
        }
    }

    #endregion
}

