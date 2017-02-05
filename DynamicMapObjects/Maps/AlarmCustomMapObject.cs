using System;
using Genetec.Sdk.Entities.Maps;

// ==========================================================================
// Copyright (C) 1989-2015 by Genetec Information Systems, Inc.
// All rights reserved.
// May be used only in accordance with a valid Source Code License Agreement.
// Developer: JD Trepanier
// ==========================================================================
namespace DynamicMapObjects.Maps
{
    [MapObject(IdString, typeof(AlarmCustomMapObjectView), false)]
    public sealed class AlarmCustomMapObject : MapObject
    {
        #region Constants

        private new static readonly Guid Id = new Guid(IdString);

        private const string IdString = "{CCD7A714-2E93-4BB6-8C30-3A8C11A8E6B6}";

        public static readonly Guid AlarmLayerID = new Guid("{F5DE347D-BE1F-4C37-B977-50C0C7D9EE8A}");

        #endregion

        #region Properties

        public override Guid LayerId { get { return AlarmLayerID; } }

        #endregion

        #region Constructors

        public AlarmCustomMapObject() : base(Id)
        {
        }

        public AlarmCustomMapObject(Guid linkedEntity) : base(Id)
        {
            LinkedEntity = linkedEntity;
        }


        public override bool IsClusterable { get { return true; } }

        #endregion
    }
}