import { useRef, useState } from 'react'
import { Button, Space, Tag, Modal, message, Card } from 'antd'
import {
  PlusOutlined,
  DeleteOutlined,
  DownloadOutlined,
  ExclamationCircleOutlined,
  EyeOutlined,
} from '@ant-design/icons'
import type { ProColumns, ActionType } from '@ant-design/pro-components'
import { ProTable } from '@ant-design/pro-components'
import { equipmentApi, type EquipmentVO, type EquipmentQuery } from '../../api/equipment'
import { downloadBlob } from '../../utils/download'
import EquipmentForm from './EquipmentForm'

/** 设备状态颜色映射 */
const STATUS_COLOR_MAP: Record<string, string> = {
  normal: 'success',
  maintenance: 'warning',
  calibration: 'processing',
  scrap: 'default',
}

/** 设备状态选项 */
const STATUS_OPTIONS = [
  { label: '正常', value: 'normal' },
  { label: '维修中', value: 'maintenance' },
  { label: '校准中', value: 'calibration' },
  { label: '报废', value: 'scrap' },
]

/**
 * 设备管理页面
 */
export default function EquipmentPage() {
  const actionRef = useRef<ActionType>(null)
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([])
  const [formOpen, setFormOpen] = useState(false)
  const [formData, setFormData] = useState<EquipmentVO | undefined>(undefined)
  const [detailOpen, setDetailOpen] = useState(false)
  const [detailData, setDetailData] = useState<EquipmentVO | undefined>(undefined)

  /** 新增 */
  const handleAdd = () => {
    setFormData(undefined)
    setFormOpen(true)
  }

  /** 编辑 */
  const handleEdit = (record: EquipmentVO) => {
    setFormData(record)
    setFormOpen(true)
  }

  /** 查看详情 */
  const handleDetail = async (record: EquipmentVO) => {
    try {
      const res = await equipmentApi.detail(record.id)
      setDetailData(res)
      setDetailOpen(true)
    } catch {
      // error handled by interceptor
    }
  }

  /** 删除 */
  const handleDelete = async (ids: number[]) => {
    Modal.confirm({
      title: '确认删除',
      icon: <ExclamationCircleOutlined />,
      content: `确定删除选中的 ${ids.length} 个设备吗？删除后不可恢复。`,
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await equipmentApi.deleteBatch(ids)
          message.success('删除成功')
          actionRef.current?.reload()
          setSelectedRowKeys([])
        } catch {
          // error handled by interceptor
        }
      },
    })
  }

  /** 导出 */
  const handleExport = async () => {
    try {
      const res = await equipmentApi.export({})
      downloadBlob(res, '设备列表.xlsx')
      message.success('导出成功')
    } catch {
      // error handled by interceptor
    }
  }

  /** 表单成功回调 */
  const handleFormSuccess = () => {
    setFormOpen(false)
    setFormData(undefined)
    actionRef.current?.reload()
  }

  /** 详情关闭回调 */
  const handleDetailClose = () => {
    setDetailOpen(false)
    setDetailData(undefined)
  }

  const columns: ProColumns<EquipmentVO>[] = [
    {
      title: '设备编号',
      dataIndex: 'equipmentNo',
      width: 160,
      ellipsis: true,
    },
    {
      title: '设备名称',
      dataIndex: 'name',
      width: 160,
      ellipsis: true,
      render: (_, record) => <a onClick={() => handleDetail(record)}>{record.name}</a>,
    },
    {
      title: '型号',
      dataIndex: 'model',
      width: 130,
      ellipsis: true,
    },
    {
      title: '制造商',
      dataIndex: 'manufacturer',
      width: 150,
      ellipsis: true,
    },
    {
      title: '序列号',
      dataIndex: 'serialNo',
      width: 150,
      ellipsis: true,
      search: false,
    },
    {
      title: '存放位置',
      dataIndex: 'location',
      width: 130,
      ellipsis: true,
      search: false,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      valueType: 'select',
      valueEnum: {
        normal: { text: '正常', status: 'Success' },
        maintenance: { text: '维修中', status: 'Warning' },
        calibration: { text: '校准中', status: 'Processing' },
        scrap: { text: '报废', status: 'Default' },
      },
      render: (_, record) => (
        <Tag color={STATUS_COLOR_MAP[record.status] || 'default'}>
          {record.statusName || record.status}
        </Tag>
      ),
    },
    {
      title: '上次校准日期',
      dataIndex: 'lastCalibration',
      width: 130,
      valueType: 'date',
      search: false,
    },
    {
      title: '下次校准日期',
      dataIndex: 'calibrationDue',
      width: 130,
      valueType: 'date',
      search: false,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 170,
      valueType: 'dateTime',
      search: false,
    },
    {
      title: '备注',
      dataIndex: 'remark',
      width: 150,
      ellipsis: true,
      search: false,
    },
    {
      title: '操作',
      width: 160,
      fixed: 'right',
      search: false,
      render: (_, record) => (
        <Space>
          <a onClick={() => handleDetail(record)}>
            <EyeOutlined /> 详情
          </a>
          <a onClick={() => handleEdit(record)}>编辑</a>
          <a onClick={() => handleDelete([record.id])} style={{ color: '#ff4d4f' }}>
            删除
          </a>
        </Space>
      ),
    },
  ]

  return (
    <Card>
      <ProTable<EquipmentVO, EquipmentQuery>
        actionRef={actionRef}
        columns={columns}
        rowKey="id"
        request={async (params) => {
          const { current, pageSize, ...rest } = params
          const res = await equipmentApi.page({
            pageNum: current,
            pageSize,
            keyword: rest.keyword,
            status: rest.status,
            location: rest.location,
            calibrationDueStart: rest.calibrationDueStart,
            calibrationDueEnd: rest.calibrationDueEnd,
          })
          return {
            data: res.records,
            total: res.total,
            success: true,
          }
        }}
        toolbar={{
          actions: [
            <Button key="add" type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
              新增设备
            </Button>,
            <Button
              key="export"
              icon={<DownloadOutlined />}
              onClick={handleExport}
            >
              导出Excel
            </Button>,
            <Button
              key="delete"
              danger
              icon={<DeleteOutlined />}
              disabled={selectedRowKeys.length === 0}
              onClick={() => handleDelete(selectedRowKeys as number[])}
            >
              批量删除
            </Button>,
          ],
        }}
        rowSelection={{
          selectedRowKeys,
          onChange: (keys) => setSelectedRowKeys(keys),
        }}
        pagination={{
          showSizeChanger: true,
          showQuickJumper: true,
          pageSizeOptions: ['10', '20', '50'],
        }}
        search={{
          labelWidth: 'auto',
          defaultCollapsed: true,
        }}
        dateFormatter="string"
      />

      {/* 新增/编辑弹窗 */}
      <EquipmentForm
        open={formOpen}
        data={formData}
        onSuccess={handleFormSuccess}
        onCancel={() => {
          setFormOpen(false)
          setFormData(undefined)
        }}
      />

      {/* 详情抽屉 */}
      <Modal
        title="设备详情"
        open={detailOpen}
        onCancel={handleDetailClose}
        footer={null}
        width={680}
        destroyOnClose
      >
        {detailData && (
          <div style={{ padding: '16px 0' }}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
              <div>
                <div style={{ color: '#8c8c8c', marginBottom: 4 }}>设备编号</div>
                <div>{detailData.equipmentNo}</div>
              </div>
              <div>
                <div style={{ color: '#8c8c8c', marginBottom: 4 }}>设备名称</div>
                <div>{detailData.name}</div>
              </div>
              <div>
                <div style={{ color: '#8c8c8c', marginBottom: 4 }}>型号</div>
                <div>{detailData.model || '-'}</div>
              </div>
              <div>
                <div style={{ color: '#8c8c8c', marginBottom: 4 }}>制造商</div>
                <div>{detailData.manufacturer || '-'}</div>
              </div>
              <div>
                <div style={{ color: '#8c8c8c', marginBottom: 4 }}>序列号</div>
                <div>{detailData.serialNo || '-'}</div>
              </div>
              <div>
                <div style={{ color: '#8c8c8c', marginBottom: 4 }}>存放位置</div>
                <div>{detailData.location || '-'}</div>
              </div>
              <div>
                <div style={{ color: '#8c8c8c', marginBottom: 4 }}>状态</div>
                <div>
                  <Tag color={STATUS_COLOR_MAP[detailData.status] || 'default'}>
                    {detailData.statusName || detailData.status}
                  </Tag>
                </div>
              </div>
              <div>
                <div style={{ color: '#8c8c8c', marginBottom: 4 }}>上次校准日期</div>
                <div>{detailData.lastCalibration || '-'}</div>
              </div>
              <div>
                <div style={{ color: '#8c8c8c', marginBottom: 4 }}>下次校准日期</div>
                <div>{detailData.calibrationDue || '-'}</div>
              </div>
              <div>
                <div style={{ color: '#8c8c8c', marginBottom: 4 }}>创建时间</div>
                <div>{detailData.createTime || '-'}</div>
              </div>
            </div>
            {detailData.remark && (
              <div style={{ marginTop: 16 }}>
                <div style={{ color: '#8c8c8c', marginBottom: 4 }}>备注</div>
                <div>{detailData.remark}</div>
              </div>
            )}
          </div>
        )}
      </Modal>
    </Card>
  )
}
