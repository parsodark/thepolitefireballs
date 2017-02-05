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
    public partial class AlarmCustomMapObjectView
    {
        #region Constants

        public const string AlarmLayerName = "AlarmCustom";

        #endregion

        public bool m_canDragDrop = false;

        #region Constructors

        public AlarmCustomMapObjectView()
        {
            InitializeComponent();
        }

        #endregion

        private void OnAlarmCustomMapObjectViewMouseDown(object sender, MouseButtonEventArgs e)
        {
            m_canDragDrop = true;
        }

        private void OnAlarmCustomMapObjectViewMouseMove(object sender, MouseEventArgs e)
        {
            MapObjectView alarmMapObject = sender as MapObjectView;
            if (alarmMapObject != null && m_canDragDrop && e.LeftButton == MouseButtonState.Pressed)
            {
                if (DragDrop.DoDragDrop(this, alarmMapObject.MapObject.LinkedEntity, DragDropEffects.Copy) == DragDropEffects.None)
                {
                    m_canDragDrop = false;
                }
            }
        }
    }

    #endregion
}

